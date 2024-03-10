package com.bluewhaletech.Ourry;

import com.bluewhaletech.Ourry.domain.*;
import com.bluewhaletech.Ourry.exception.QuestionNotFoundException;
import com.bluewhaletech.Ourry.exception.VoteNotFoundException;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.*;
import com.bluewhaletech.Ourry.service.MemberServiceImpl;
import com.bluewhaletech.Ourry.util.RedisBlackListManagement;
import com.bluewhaletech.Ourry.util.RedisEmailAuthentication;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	private ChoiceRepository choiceRepository;
	@Autowired
	private VoteRepository voteRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private SolutionJpaRepository solutionJpaRepository;
	@Autowired
	private RedisJwtRepository redisJwtRepository;
	@Autowired
	private RedisBlackListManagement redisBlackListManagement;

	@Test
	@Transactional
	void getQuestionListTest() {
		//given
		Member member = Member.builder()
				.email("sth488111@naver.com")
				.password("1234")
				.nickname("닉네임")
				.phone("01044748813")
				.role(MemberRole.USER)
				.build();
		memberRepository.save(member);

		Category category = Category.builder()
				.name("카테고리명")
				.build();
		categoryRepository.save(category);

		Question question = Question.builder()
				.title("질문제목")
				.content("질문내용")
				.member(member)
				.category(category)
				.build();
		questionRepository.save(question);

		List<Choice> choices = new ArrayList<>();
		Choice c1 = Choice.builder()
				.question(question)
				.detail("화해하지 않고 자연스럽게 거리를 둔다.")
				.seq(1)
				.build();
		choiceRepository.save(c1);
		choices.add(c1);
		Choice c2 = Choice.builder()
				.question(question)
				.detail("화해하고 관계를 유지한다")
				.seq(2)
				.build();
		choiceRepository.save(c2);
		choices.add(c2);

		//when
		Question q = questionRepository.findAll().get(0);

		//then
		System.out.println("q.getTitle = " + q.getTitle());
		System.out.println("question.getTitle() = " + question.getTitle());
		Assertions.assertThat(q.getTitle()).isEqualTo(question.getTitle());
	}

	@Test
	@Transactional
	void getQuestionDetailTest() {
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
				.name("카테고리명")
				.build();
		categoryRepository.save(category);

		Question question = Question.builder()
				.title("질문제목")
				.content("질문내용")
				.member(member)
				.category(category)
				.build();
		questionRepository.save(question);

		Choice c1 = Choice.builder()
				.question(question)
				.detail("화해하지 않고 자연스럽게 거리를 둔다.")
				.seq(1)
				.build();
		choiceRepository.save(c1);
		Choice c2 = Choice.builder()
				.question(question)
				.detail("화해하고 관계를 유지한다")
				.seq(2)
				.build();
		choiceRepository.save(c2);

		//when
		Question q = questionRepository.findOne(question.getQuestionId());

		//then
		System.out.println("q.getTitle() = " + q.getTitle());
		System.out.println("question.getTitle() = " + question.getTitle());
		Assertions.assertThat(q.getTitle()).isEqualTo(question.getTitle());
	}

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

		//when
		Question question = Question.builder()
				.title("테스트제목")
				.content("테스트내용")
				.member(member)
				.category(category)
				.build();
		Long questionId = questionRepository.save(question);
		Question q = Optional.ofNullable(questionRepository.findOne(questionId))
				.orElseThrow(() -> new QuestionNotFoundException("질문 정보를 불러올 수 없습니다."));

		//then
		Assertions.assertThat(q.getTitle()).isEqualTo(question.getTitle());
	}

	@Test
	@Transactional
	void answerQuestion() {
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
				.name("카테고리명")
				.build();
		categoryRepository.save(category);

		Question question = Question.builder()
				.title("질문제목")
				.content("질문내용")
				.member(member)
				.category(category)
				.build();
		questionRepository.save(question);

		Choice c1 = Choice.builder()
				.question(question)
				.detail("화해하지 않고 자연스럽게 거리를 둔다.")
				.seq(1)
				.build();
		choiceRepository.save(c1);

		Choice c2 = Choice.builder()
				.question(question)
				.detail("화해하고 관계를 유지한다")
				.seq(2)
				.build();
		choiceRepository.save(c2);

		//when
		Vote vote = Vote.builder()
				.member(member)
				.question(question)
				.choice(c2)
				.build();
		Long voteId = voteRepository.save(vote);
		Solution solution = Solution.builder()
				.opinion("내 생각은 이렇다.")
				.vote(vote)
				.build();
		solutionRepository.save(solution);

		//then
		Vote v = voteRepository.findOne(voteId);
		Solution s = solutionJpaRepository.findByVote(v);
		System.out.println("v.getChoice().getDetail() = " + v.getChoice().getDetail());
		System.out.println("vote.getChoice().getDetail() = " + vote.getChoice().getDetail());
		System.out.println("s.getOpinion() = " + s.getOpinion());
		System.out.println("solution.getOpinion() = " + solution.getOpinion());
		Assertions.assertThat(solution.getOpinion()).isEqualTo(s.getOpinion());
		Assertions.assertThat(vote.getChoice().getDetail()).isEqualTo(v.getChoice().getDetail());
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