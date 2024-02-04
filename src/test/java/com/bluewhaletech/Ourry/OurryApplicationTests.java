package com.bluewhaletech.Ourry;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.MemberRole;
import com.bluewhaletech.Ourry.domain.RefreshToken;
import com.bluewhaletech.Ourry.dto.AuthenticationDTO;
import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.MemberRepository;
import com.bluewhaletech.Ourry.repository.RedisJwtRepository;
import com.bluewhaletech.Ourry.service.MemberServiceImpl;
import com.bluewhaletech.Ourry.util.RedisBlackListManagement;
import com.bluewhaletech.Ourry.util.RedisEmailAuthentication;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;

@SpringBootTest
class OurryApplicationTests {
	@Autowired
	private JwtProvider tokenProvider;
	@Autowired
	private RedisEmailAuthentication redisUtil;
	@Autowired
	private AuthenticationManagerBuilder authenticationManagerBuilder;
	@Autowired
	private MemberServiceImpl memberService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RedisJwtRepository redisJwtRepository;
	@Autowired
	private RedisBlackListManagement redisBlackListManagement;

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

//	@Test
//	@Transactional
//	@DisplayName("JWT 발급 테스트")
//	void issueTokenTest() {
//		//given
//		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(
//				"MyNickNameIsBlueWhaleAndRealNameIsParanAndWeUseAccessTokenAndRefreshTokenBoth"
//		));
//
//		Member member = Member.builder()
//				.memberId(1L)
//				.email("abc@naver.com")
//				.password("1234")
//				.nickname("Para")
//				.phone("01044748813")
//				.role(MemberRole.USER)
//				.build();
//		memberRepository.save(member);
//
//		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
//				new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword())
//		);
//
//		//when
//		JwtDTO jwt = tokenProvider.createToken(authentication);
//
//		//then
//		Assertions.assertThat(jwt).isNotNull();
//		Jws<Claims> claims = Jwts.parser()
//				.verifyWith(secretKey)
//				.build()
//				.parseSignedClaims(jwt.getAccessToken());
//
//		/* Subject(이메일) 비교 */
//		Assertions.assertThat(claims.getPayload().getSubject()).isEqualTo("abc@naver.com");
//	}
	
//	@Test
//	@Transactional
//	@DisplayName("JWT 만료여부 테스트")
//	void checkTokenExpirationTest() {
//		//given
//		SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(
//				"MyNickNameIsBlueWhaleAndRealNameIsParanAndWeUseAccessTokenAndRefreshTokenBoth"
//		));
//
//		Member member = Member.builder()
//				.memberId(1L)
//				.email("abc@naver.com")
//				.password("1234")
//				.nickname("Para")
//				.phone("01044748813")
//				.role(MemberRole.USER)
//				.build();
//		memberRepository.save(member);
//
//
//		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
//				new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword())
//		);
//
//		JwtDTO jwt = tokenProvider.createToken(authentication);
//
//		//when
//		Date now = new Date();
//		Date expiration = Jwts.parser()
//				.verifyWith(secretKey)
//				.build()
//				.parseSignedClaims(jwt.getAccessToken())
//				.getPayload()
//				.getExpiration();
//
//		//then
//		System.out.println("now : " + now);
//		System.out.println("exp : " + expiration);
//		Assertions.assertThat(expiration).isAfter(now);
//	}

	@Test
	@Transactional
	@DisplayName("JWT Refresh Token 유효성 확인")
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
		AuthenticationDTO authenticationDTO = AuthenticationDTO.builder()
				.tokenId(member.getMemberId())
				.tokenName(member.getEmail())
				.authentication(authentication)
				.build();
		JwtDTO jwt = tokenProvider.createToken(authenticationDTO);

		//when
		String refreshToken = jwt.getRefreshToken();
		redisJwtRepository.save(RefreshToken.builder()
				.tokenId(member.getMemberId())
				.tokenValue(jwt.getRefreshToken())
				.expiration(jwt.getRefreshTokenExpiration())
				.build());

		//then
		RefreshToken storedRefreshToken = redisJwtRepository.findById(member.getMemberId())
				.orElseThrow(() -> new JwtException("Refresh Token이 존재하지 않습니다."));
		System.out.println("refreshToken : " + refreshToken);
		System.out.println("storedRefreshToken : " + storedRefreshToken.getTokenValue());
		Assertions.assertThat(storedRefreshToken.getTokenValue()).isEqualTo(refreshToken);
	}

	@Test
	@Transactional()
	@DisplayName("로그아웃한 회원 블랙리스트 추가여부 테스트")
	void blackListAdditionTest() {
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
		AuthenticationDTO authenticationDTO = AuthenticationDTO.builder()
				.tokenId(member.getMemberId())
				.tokenName(member.getEmail())
				.authentication(authentication)
				.build();
		JwtDTO jwt = tokenProvider.createToken(authenticationDTO);

		//when
		String accessToken = jwt.getAccessToken();
		String refreshToken = jwt.getRefreshToken();
		memberService.memberLogout(accessToken, refreshToken);

		//then
		Assertions.assertThat("LOGOUT").isEqualTo(redisBlackListManagement.checkLogout(accessToken));
	}
}