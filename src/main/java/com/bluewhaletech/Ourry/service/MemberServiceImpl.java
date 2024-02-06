package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.MemberRole;
import com.bluewhaletech.Ourry.domain.RefreshToken;
import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.exception.*;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.JpaMemberRepository;
import com.bluewhaletech.Ourry.repository.MemberRepository;
import com.bluewhaletech.Ourry.repository.RedisJwtRepository;
import com.bluewhaletech.Ourry.util.RedisBlackListManagement;
import com.bluewhaletech.Ourry.util.RedisEmailAuthentication;
import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {
    private final JwtProvider tokenProvider;
    private final MailServiceImpl mailService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JpaMemberRepository jpaMemberRepository;
    private final RedisJwtRepository redisJwtRepository;
    private final RedisEmailAuthentication redisEmailAuthentication;
    private final RedisBlackListManagement redisBlackListManagement;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public MemberServiceImpl(JwtProvider tokenProvider, MailServiceImpl mailService, PasswordEncoder passwordEncoder, MemberRepository memberRepository, JpaMemberRepository jpaMemberRepository, RedisJwtRepository redisJwtRepository, RedisEmailAuthentication redisEmailAuthentication, RedisBlackListManagement redisBlackListManagement, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.jpaMemberRepository = jpaMemberRepository;
        this.redisJwtRepository = redisJwtRepository;
        this.redisEmailAuthentication = redisEmailAuthentication;
        this.redisBlackListManagement = redisBlackListManagement;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Transactional
    public String createAccount(MemberRegistrationDTO dto) {
        /* 이메일 중복 확인 */
        jpaMemberRepository.findByEmail(dto.getEmail())
                .ifPresent(member -> {
                    throw new MemberEmailDuplicationException("중복되는 이메일이 존재합니다.");
                });

        /* 이메일 인증여부 확안 */
        if(redisEmailAuthentication.getAuthenticationCode(dto.getEmail()) == null || !redisEmailAuthentication.checkAuthentication(dto.getEmail()).equals("Y")) {
            throw new EmailAuthenticationNotCompletedException("이메일 인증이 완료되지 않았습니다.");
        }

        /* 회원가입 */
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .role(MemberRole.USER)
                .build();
        memberRepository.save(member);
        return "SUCCESS";
    }

    @Transactional
    public JwtDTO memberLogin(MemberLoginDTO dto) {
        /* 이메일 유효성 확인 */
        Member member = jpaMemberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EmailIncorrectException("이메일 주소가 올바르지 않습니다."));

        /* 비밀번호 일치 확인 */
        if(!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        /* 이메일 & 비밀번호를 바탕으로 인증(Authentication) 정보 생성 및 JWT 발급 */
        return memberAuthentication(member);
    }

    @Transactional
    public JwtDTO reissueToken(String refreshToken) {
        /* Redis 내부에 Refresh Token 존재하는지 확인 */
        Long tokenId = tokenProvider.getTokenId(refreshToken);
        RefreshToken storedRefreshToken = redisJwtRepository.findById(tokenId)
                .orElseThrow(() -> new JwtException("존재하지 않는 Refresh Token입니다."));

        /* Redis에 저장된 Refresh Token 정보와 요청으로부터 받아온 Refresh Token 정보가 일치하는지 확인 */
        if(!refreshToken.equals(storedRefreshToken.getTokenValue())) {
            throw new JwtTokenMismatchException("Refresh Token 정보가 일치하지 않습니다.");
        }

        /* Refresh Token으로부터 사용자(Member) 정보 가져오기 */
        Member member = memberRepository.findOne(tokenProvider.getTokenId(refreshToken))
                .orElseThrow(() -> new MemberNotFoundException("Refresh Token으로 조회되는 사용자가 없습니다."));

        /* 이메일 & 비밀번호를 바탕으로 인증(Authentication) 정보 생성 및 JWT 발급 */
        return memberAuthentication(member);
    }

    @Transactional
    public String memberLogout(String accessToken) {
        /* Access Token으로부터 Subject(Email) 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken);
        /* Email로부터 Member ID 가져오기 */
        Member member = jpaMemberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        /* Redis 내부에 저장된 Refresh Token 값 삭제 */
        redisJwtRepository.deleteById(member.getMemberId());
        /* Redis 내부에 해당 Access Token 값을 BlackList로 저장 */
        redisBlackListManagement.setAccessTokenExpire(accessToken, tokenProvider.getTokenExpiration(accessToken));
        return "SUCCESS";
    }

    @Transactional
    public String sendAuthenticationCode(EmailAddressDTO dto) throws MessagingException, UnsupportedEncodingException {
        /* 이메일 중복 확인 */
        jpaMemberRepository.findByEmail(dto.getEmail())
                .ifPresent(member -> {
                    throw new MemberEmailDuplicationException("중복되는 이메일이 존재합니다.");
                });

        /* 인증코드 생성 및 유효기간 5분으로 설정 */
        String code = createRandomCode();
        /* Redis 내부에 생성한 인증코드 저장 */
        redisEmailAuthentication.setAuthenticationExpire(dto.getEmail(), code, 5L);

        String text = "";
        text += "안녕하세요. Ourry입니다.";
        text += "<br/><br/>";
        text += "인증코드 보내드립니다.";
        text += "<br/><br/>";
        text += "인증코드 : <b>"+code+"</b>";

        EmailDTO data = EmailDTO.builder()
                .email(dto.getEmail())
                .title("이메일 인증코드 발송 메일입니다.")
                .text(text)
                .build();

        /* 입력한 이메일로 인증코드 발송 */
        mailService.sendMail(data);
        return "SUCCESS";
    }

    @Transactional
    public String emailAuthentication(EmailAuthenticationDTO dto) {
        /* 회원 이메일로 전송된 인증코드 */
        String code = redisEmailAuthentication.getAuthenticationCode(dto.getEmail());

        /* Redis 내부에 이메일이 존재하는지 확인 */
        if(code == null) {
            throw new MemberNotFoundException("등록되지 않은 이메일입니다.");
        }

        /* 입력한 인증코드와 발송된 인증코드 값 비교 */
        if(!code.equals(dto.getCode())) {
            throw new EmailAuthenticationCodeMismatchException("이메일 인증코드가 일치하지 않습니다.");
        }

        /* 이메일 인증 완료 처리 */
        redisEmailAuthentication.setAuthenticationComplete(dto.getEmail());
        return "SUCCESS";
    }

    @Transactional
    public String passwordReset(PasswordResetDTO dto) {
        /* 이메일(회원) 존재 확인 */
        Member member = jpaMemberRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        /* 이메일 인증여부 확안 */
        if(redisEmailAuthentication.getAuthenticationCode(member.getEmail()) == null || !redisEmailAuthentication.checkAuthentication(member.getEmail()).equals("Y")) {
            throw new EmailAuthenticationNotCompletedException("이메일 인증이 완료되지 않았습니다.");
        }

        /* '새 비밀번호'와 '새 비밀번호확인' 값 비교 */
        if(!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        /* 회원 비밀번호 변경 */
        member.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        memberRepository.save(member);
        return "SUCCESS";
    }

    @Transactional
    public String updateProfile(MemberDTO dto) {
        Member member = jpaMemberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EmailIncorrectException("등록되지 않은 이메일입니다."));
        return "SUCCESS";
    }

    private JwtDTO memberAuthentication(Member member) {
        /* 이메일 & 비밀번호를 바탕으로 인증(Authentication) 정보 생성 */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword())
        );
        AuthenticationDTO authenticationDTO = AuthenticationDTO.builder()
                .tokenId(member.getMemberId())
                .tokenName(member.getEmail())
                .authentication(authentication)
                .build();

        /* JWT 토큰 발급 */
        JwtDTO token = tokenProvider.createToken(authenticationDTO);
        /* Redis 내부에 Refresh Token 갱신 */
        redisJwtRepository.save(RefreshToken.builder()
                .tokenId(member.getMemberId())
                .tokenValue(token.getRefreshToken())
                .expiration(token.getRefreshTokenExpiration())
                .build());
        return token;
    }

    private String createRandomCode() {
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for(int i=0; i<6; i++) {
            int n = random.nextInt(36);
            if(n>25) sb.append((n-25));
            else sb.append(((char)(n+65)));
        }

        return sb.toString();
    }
}