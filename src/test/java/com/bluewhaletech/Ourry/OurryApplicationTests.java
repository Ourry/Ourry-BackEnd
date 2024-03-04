package com.bluewhaletech.Ourry;

import com.bluewhaletech.Ourry.domain.*;
import com.bluewhaletech.Ourry.exception.QuestionNotFoundException;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.*;
import com.bluewhaletech.Ourry.service.MemberServiceImpl;
import com.bluewhaletech.Ourry.util.RedisBlackListManagement;
import com.bluewhaletech.Ourry.util.RedisEmailAuthentication;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

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
	private QuestionRepository questionRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private RedisJwtRepository redisJwtRepository;
	@Autowired
	private RedisBlackListManagement redisBlackListManagement;

	@Test
	@Transactional
	void addQuestionTest() {
		//given
		Member member = Member.builder()
				.email("sth48811@naver.com")
				.password("1234")
				.nickname("닉네임")
				.phone("01044748813")
				.role(MemberRole.USER)
				.build();
		memberRepository.save(member);

		Category category = Category.builder()
				.name("사회생활")
				.build();
		categoryRepository.save(category);

		Question question = Question.builder()
				.title("테스트제목")
				.content("테스트내용")
				.isSolved('N')
				.member(member)
				.category(category)
				.build();
		Long questionId = questionRepository.save(question);

		//when
		Question q = questionRepository.findOne(questionId)
				.orElseThrow(() -> new QuestionNotFoundException("질문 정보를 불러올 수 없습니다."));

		//then
		Assertions.assertThat(q.getTitle()).isEqualTo(question.getTitle());
	}

	@Test
	@Transactional
	void answerQuestion() throws Exception {
		//given
		Member member = Member.builder()
				.email("sth48811@naver.com")
				.password("1234")
				.nickname("닉네임")
				.phone("01044748813")
				.role(MemberRole.USER)
				.build();
		memberRepository.save(member);

		Choice choice = Choice.builder()
				.detail("대인배스럽게 화해한다")
				.seq(2)
				.build();

		Solution solution = Solution.builder()
				.opinion("화해하는 방법이 내 이미지를 소모하지 않으면서 이기는 방법이다.")
				.member(member)
				.choice(choice)
				.build();
		Long solutionId = solutionRepository.save(solution);

		//when
		Solution s = solutionRepository.findOne(solutionId)
				.orElseThrow(Exception::new);

		//then
		Assertions.assertThat(solution.getOpinion()).isEqualTo(s.getOpinion());
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

//	@Test
//	@Transactional
//	@DisplayName("JWT Refresh Token 유효성 확인")
//	void checkTokenValidationTest() {
//		//given
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
//		AuthenticationDTO authenticationDTO = AuthenticationDTO.builder()
//				.tokenId(member.getMemberId())
//				.tokenName(member.getEmail())
//				.authentication(authentication)
//				.build();
//		JwtDTO jwt = tokenProvider.createToken(authenticationDTO);
//
//		//when
//		String refreshToken = jwt.getRefreshToken();
//		redisJwtRepository.save(RefreshToken.builder()
//				.tokenId(member.getMemberId())
//				.tokenValue(jwt.getRefreshToken())
//				.expiration(jwt.getRefreshTokenExpiration())
//				.build());
//
//		//then
//		RefreshToken storedRefreshToken = redisJwtRepository.findById(member.getMemberId())
//				.orElseThrow(() -> new JwtException("Refresh Token이 존재하지 않습니다."));
//		System.out.println("refreshToken : " + refreshToken);
//		System.out.println("storedRefreshToken : " + storedRefreshToken.getTokenValue());
//		Assertions.assertThat(storedRefreshToken.getTokenValue()).isEqualTo(refreshToken);
//	}

//	@Test
//	@Transactional()
//	@DisplayName("로그아웃한 회원 블랙리스트 추가여부 테스트")
//	void blackListAdditionTest() {
//		//given
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
//		AuthenticationDTO authenticationDTO = AuthenticationDTO.builder()
//				.tokenId(member.getMemberId())
//				.tokenName(member.getEmail())
//				.authentication(authentication)
//				.build();
//		JwtDTO jwt = tokenProvider.createToken(authenticationDTO);
//
//		//when
//		String accessToken = jwt.getAccessToken();
//		memberService.memberLogout(accessToken);
//
//		//then
//		Assertions.assertThat("LOGOUT").isEqualTo(redisBlackListManagement.checkLogout(accessToken));
//	}

//	@Test
//	@Transactional
//	@DisplayName("Refresh Token 재발급 테스트")
//	void reissueTest() {
//		//given
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
//		AuthenticationDTO authenticationDTO = AuthenticationDTO.builder()
//				.tokenId(member.getMemberId())
//				.tokenName(member.getEmail())
//				.authentication(authentication)
//				.build();
//		JwtDTO jwt = tokenProvider.createToken(authenticationDTO);
//		redisJwtRepository.save(RefreshToken.builder()
//				.tokenId(member.getMemberId())
//				.tokenValue(jwt.getRefreshToken())
//				.expiration(jwt.getRefreshTokenExpiration())
//				.build());
//
//		//when
//		JwtDTO newJwt = memberService.reissueToken(jwt.getRefreshToken());
//
//		//then
//		Long atk = tokenProvider.getTokenExpiration(jwt.getAccessToken());
//		Long rtk = tokenProvider.getTokenExpiration(jwt.getRefreshToken());
//		Long atkExpiration = tokenProvider.getTokenExpiration(newJwt.getAccessToken());
//		Long rtkExpiration = tokenProvider.getTokenExpiration(newJwt.getRefreshToken());
//		System.out.println(atk + " " + atkExpiration);
//		System.out.println(rtk + " " + rtkExpiration);
//	}
}