package com.bluewhaletech.Ourry;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.MemberRole;
import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.MemberRepository;
import com.bluewhaletech.Ourry.service.MemberServiceImpl;
import com.bluewhaletech.Ourry.util.RedisEmailAuthentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Date;

@SpringBootTest
class OurryApplicationTests {
	@Autowired
	private JwtProvider tokenProvider;
	@Autowired
	private RedisEmailAuthentication redisUtil;
	@Autowired
	private AuthenticationManagerBuilder authenticationManagerBuilder;
	@Autowired
	private MemberRepository memberRepository;

	@Test
	void redisTest() {
		//given
		String email = "sth4881@naver.com";
		String code = "aaa111";

		//when
		redisUtil.setAuthenticationExpire(email, code, 5L);

		//then
		Assertions.assertThat(code).isEqualTo(redisUtil.getAuthenticationCode(email));
	}

	@Test
	@Transactional
	@DisplayName("JWT 발급 테스트")
	void issueTokenTest() {
		//given
		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(
				"MyNickNameIsBlueWhaleAndRealNameIsParanAndWeUseAccessTokenAndRefreshTokenBoth"
		));

		Member member = Member.builder()
				.memberId(1L)
				.email("abc@naver.com")
				.password("1234")
				.nickname("Para")
				.phone("01044748813")
				.role(MemberRole.USER)
				.build();
		memberRepository.save(member);

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
				new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword())
		);

		//when
		JwtDTO jwt = tokenProvider.createToken(authentication);

		//then
		Assertions.assertThat(jwt).isNotNull();
		Jws<Claims> claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(jwt.getAccessToken());

		/* Subject(이메일) 비교 */
		Assertions.assertThat(claims.getPayload().getSubject()).isEqualTo("abc@naver.com");
	}
	
	@Test
	@Transactional
	@DisplayName("JWT 만료여부 테스트")
	void checkTokenExpirationTest() {
		//given
		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(
				"MyNickNameIsBlueWhaleAndRealNameIsParanAndWeUseAccessTokenAndRefreshTokenBoth"
		));

		Member member = Member.builder()
				.memberId(1L)
				.email("abc@naver.com")
				.password("1234")
				.nickname("Para")
				.phone("01044748813")
				.role(MemberRole.USER)
				.build();
		memberRepository.save(member);

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
				new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword())
		);

		JwtDTO jwt = tokenProvider.createToken(authentication);

		//when
		Date now = new Date();
		Date expiration = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(jwt.getAccessToken())
				.getPayload()
				.getExpiration();

		//then
		System.out.println("now : " + now);
		System.out.println("exp : " + expiration);
		Assertions.assertThat(expiration).isAfter(now);
	}

	@Test
	@Transactional
	@DisplayName("JWT 인증 정보 확인")
	void checkTokenValidationTest() {
		//given
		Member member = Member.builder()
				.memberId(1L)
				.email("abc@naver.com")
				.password("1234")
				.nickname("Para")
				.phone("01044748813")
				.role(MemberRole.USER)
				.build();
		memberRepository.save(member);

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
				new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword())
		);

		JwtDTO jwt = tokenProvider.createToken(authentication);

		//when
		String email = "abc@naver.com";
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
		Authentication auth = tokenProvider.getAuthentication(jwt.getAccessToken());

		//then
		System.out.println("auth.getName() : " + auth.getName() + " == " + email);
		Assertions.assertThat(auth.getName()).isEqualTo(email);
		System.out.println("auth.getAuthorities() : " + auth.getAuthorities().toString() + " == " + authority);
		Assertions.assertThat(auth.getAuthorities()).isEqualTo(authority);
	}
}