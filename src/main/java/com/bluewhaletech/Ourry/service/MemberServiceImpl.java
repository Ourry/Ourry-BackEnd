package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.MemberRole;
import com.bluewhaletech.Ourry.domain.RefreshToken;
import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.exception.*;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.MemberJpaRepository;
import com.bluewhaletech.Ourry.repository.MemberRepository;
import com.bluewhaletech.Ourry.repository.RedisJwtRepository;
import com.bluewhaletech.Ourry.util.RedisBlackListManagement;
import com.bluewhaletech.Ourry.util.RedisEmailAuthentication;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {
    private final JwtProvider tokenProvider;
    private final MailServiceImpl mailService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final RedisJwtRepository redisJwtRepository;
    private final RedisEmailAuthentication redisEmailAuthentication;
    private final RedisBlackListManagement redisBlackListManagement;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public MemberServiceImpl(JwtProvider tokenProvider, MailServiceImpl mailService, PasswordEncoder passwordEncoder, MemberRepository memberRepository, MemberJpaRepository memberJpaRepository, RedisJwtRepository redisJwtRepository, RedisEmailAuthentication redisEmailAuthentication, RedisBlackListManagement redisBlackListManagement, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.memberJpaRepository = memberJpaRepository;
        this.redisJwtRepository = redisJwtRepository;
        this.redisEmailAuthentication = redisEmailAuthentication;
        this.redisBlackListManagement = redisBlackListManagement;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Override
    public MemberInfoDTO getMemberInfo(String accessToken) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        return MemberInfoDTO.builder()
                .email(email)
                .nickname(member.getNickname())
                .phone(member.getPhone())
                .createdAt(member.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void updateNickname(String accessToken, NicknameUpdateDTO dto) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        /* 비밀번호 일치 확인 */
        if(!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new PasswordConfirmException("비밀번호가 일치하지 않습니다.");
        }

        /* 닉네임 중복 확인 */
        if(memberJpaRepository.existsByNickname(dto.getNickname())) {
            throw new NicknameDuplicatedException("중복되는 닉네임이 존재합니다.");
        }

        /* 닉네임 변경 */
        member.setNickname(dto.getNickname());
    }

    @Override
    @Transactional
    public void createAccount(MemberRegistrationDTO dto, String fcmToken) {
        /* FCM 토큰 포함여부 확인 */
        if(fcmToken == null || fcmToken.isEmpty()) {
            throw new FcmTokenNotFoundException("FCM 토큰값이 비어있습니다.");
        }

        /* 이메일 중복 확인 */
        if(memberJpaRepository.existsByEmail(dto.getEmail())) {
            throw new EmailDuplicatedException("중복되는 이메일이 존재합니다.");
        }

        /* 닉네임 중복 확인 */
        if(memberJpaRepository.existsByNickname(dto.getNickname())) {
            throw new NicknameDuplicatedException("중복되는 닉네임이 존재합니다.");
        }

        /* 이메일 인증여부 확인 */
//        if(redisEmailAuthentication.getEmailAuthenticationCode(dto.getEmail()) == null || !redisEmailAuthentication.checkEmailAuthentication(dto.getEmail()).equals("Y")) {
//            throw new EmailAuthenticationNotCompletedException("이메일 인증이 완료되지 않았습니다.");
//        }

        /* 회원가입 */
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .role(MemberRole.MEMBER)
                .fcmToken(fcmToken)
                .build();
        memberRepository.save(member);

        /* Redis 내부에 저장된 이메일 인증 기록 삭제 */
        redisEmailAuthentication.deleteEmailAuthenticationHistory(member.getEmail());
    }

    @Override
    @Transactional
    public JwtDTO memberLogin(MemberLoginDTO dto, String fcmToken) {
        /* FCM 토큰 포함여부 확인 */
        if(fcmToken == null || fcmToken.isEmpty()) {
            throw new FcmTokenNotFoundException("FCM 토큰값이 비어있습니다.");
        }

        /* 이메일 유효성 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(dto.getEmail()))
                .orElseThrow(() -> new MemberNotFoundException("등록되지 않은 이메일입니다."));

        /* 비밀번호 일치 확인 */
        if(!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new PasswordConfirmException("비밀번호가 일치하지 않습니다.");
        }

        /* 로그인할 때마다 FCM 토큰 값 갱신 */
        member.setFcmToken(fcmToken);

        /* 이메일 & 비밀번호를 바탕으로 인증(Authentication) 정보 생성 및 JWT 발급 */
        return memberAuthentication(member);
    }

    @Override
    @Transactional
    public JwtDTO reissueToken(String token) {
        /* Refresh Token 인입여부 및 유효성 확인 */
        String refreshToken = resolveRefreshToken(token);

        /* Redis 내부에 Refresh Token 존재하는지 확인 */
        Long tokenId = tokenProvider.getTokenId(refreshToken);
        RefreshToken storedRefreshToken = redisJwtRepository.findById(tokenId)
                .orElseThrow(() -> new JwtNotFoundException(ErrorCode.JWT_NOT_FOUND, "존재하지 않는 Refresh Token입니다."));

        /* Redis에 저장된 Refresh Token 정보와 요청으로부터 받아온 Refresh Token 정보가 일치하는지 확인 */
        if(!refreshToken.equals(storedRefreshToken.getTokenValue())) {
            throw new JwtMismatchException(ErrorCode.JWT_MISMATCH, "Refresh Token 정보가 일치하지 않습니다.");
        }

        /* Refresh Token으로부터 사용자(Member) 정보 가져오기 */
        Member member = Optional.ofNullable(memberRepository.findOne(tokenProvider.getTokenId(refreshToken)))
                .orElseThrow(() -> new MemberNotFoundException("Refresh Token 정보로 조회되는 사용자가 없습니다."));

        /* 이메일 & 비밀번호를 바탕으로 인증(Authentication) 정보 생성 및 JWT 발급 */
        return memberAuthentication(member);
    }

    @Override
    @Transactional
    public void memberLogout(String accessToken) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

        /* Email로부터 Member ID 가져오기 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        /* Redis 내부에 저장된 Refresh Token 값 삭제 */
        redisJwtRepository.deleteById(member.getMemberId());

        /* Redis 내부에 해당 Access Token 값을 BlackList로 저장 */
        redisBlackListManagement.setAccessTokenExpire(accessToken, tokenProvider.getTokenExpiration(accessToken));
    }

    @Override
    @Transactional
    public void sendAuthenticationCode(EmailAddressDTO dto) throws MessagingException, UnsupportedEncodingException {
        /* 인증코드 생성 및 유효기간 5분으로 설정 */
        String code = createRandomCode();

        /* Redis 내부에 생성한 인증코드 저장 */
        redisEmailAuthentication.setEmailAuthenticationExpire(dto.getEmail(), code, 5L);

        String text = "";
        text += "안녕하세요. Ourry입니다.";
        text += "<br/><br/>";
        text += "인증코드 보내드립니다.";
        text += "<br/><br/>";
        text += "인증코드 : <b>"+code+"</b>";

        /* 입력한 이메일로 인증코드 발송 */
        mailService.sendMail(EmailDTO.builder()
                .email(dto.getEmail())
                .title("이메일 인증코드 발송 메일입니다.")
                .text(text)
                .build());
    }

    @Override
    @Transactional
    public void emailAuthentication(EmailAuthenticationDTO dto) {
        /* 회원 이메일로 전송된 인증코드 */
        String code = redisEmailAuthentication.getEmailAuthenticationCode(dto.getEmail());

        /* Redis 내부에 이메일이 존재하는지 확인 */
        if(code == null) {
            throw new MemberNotFoundException("등록되지 않은 이메일입니다.");
        }

        /* 입력한 인증코드와 발송된 인증코드 값 비교 */
        if(!code.equals(dto.getCode())) {
            throw new EmailAuthenticationCodeMismatchException("이메일 인증코드가 일치하지 않습니다.");
        }

        /* 이메일 인증 완료 처리 */
        redisEmailAuthentication.setEmailAuthenticationComplete(dto.getEmail());
    }

    @Override
    @Transactional
    public void passwordReset(PasswordResetDTO dto) {
        /* 이메일(회원) 존재 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(dto.getEmail()))
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        /* 이메일 인증여부 확인 */
        if(redisEmailAuthentication.getEmailAuthenticationCode(member.getEmail()) == null || !redisEmailAuthentication.checkEmailAuthentication(member.getEmail()).equals("Y")) {
            throw new EmailAuthenticationNotCompletedException("이메일 인증이 완료되지 않았습니다.");
        }

        /* '새 비밀번호'와 '새 비밀번호확인' 값 비교 */
        if(!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordConfirmException("비밀번호 확인 값이 일치하지 않습니다.");
        }

        /* 회원 비밀번호 변경 */
        member.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        /* Redis 내부에 저장된 이메일 인증 기록 삭제 */
        redisEmailAuthentication.deleteEmailAuthenticationHistory(member.getEmail());
    }

    @Override
    @Transactional
    public void addFcmToken(String accessToken, String fcmToken) {
        /* Access Token으로부터 이메일 가져오기 */
        String email = tokenProvider.getTokenSubject(accessToken.substring(7));

        /* FCM 토큰 포함여부 확인 */
        if(fcmToken == null || fcmToken.isEmpty()) {
            throw new FcmTokenNotFoundException("FCM 토큰값이 비어있습니다.");
        }

        /* 회원 존재유무 확인 */
        Member member = Optional.ofNullable(memberJpaRepository.findByEmail(email))
                .orElseThrow(() -> new MemberNotFoundException("회원 정보가 존재하지 않습니다."));

        /* FCM 토큰 갱신 */
        member.setFcmToken(fcmToken);
    }

    private JwtDTO memberAuthentication(Member member) {
        /* 이메일 & 비밀번호를 바탕으로 인증(Authentication) 정보 생성 */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword())
        );

        /* JWT 토큰 발급 */
        JwtDTO token = tokenProvider.createToken(AuthenticationDTO.builder()
                .tokenId(member.getMemberId())
                .tokenName(member.getEmail())
                .authentication(authentication)
                .build());

        /* Redis 내부에 Refresh Token 갱신 */
        redisJwtRepository.save(RefreshToken.builder()
                .tokenId(member.getMemberId())
                .tokenValue(token.getRefreshToken())
                .expiration(token.getRefreshTokenExpiration())
                .build());
        return token;
    }

    private String resolveRefreshToken(String refreshToken) {
        if(!StringUtils.hasText(refreshToken)) {
            throw new EmptyJwtException("Refresh Token 정보가 인입되지 않았습니다.");
        } else if(!refreshToken.startsWith("Bearer")) {
            throw new MalformedJwtException("유효하지 않은 JWT 토큰입니다.");
        }
        return refreshToken.substring(7);
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