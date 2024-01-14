package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.MemberRole;
import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.exception.*;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.repository.JpaMemberRepository;
import com.bluewhaletech.Ourry.repository.MemberRepository;
import com.bluewhaletech.Ourry.util.RedisUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class MemberServiceImpl implements MemberService {
    private final RedisUtil redisUtil;
    private final JwtProvider tokenProvider;
    private final MailServiceImpl mailService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JpaMemberRepository jpaMemberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public MemberServiceImpl(RedisUtil redisUtil, JwtProvider tokenProvider, MailServiceImpl mailService, PasswordEncoder passwordEncoder, MemberRepository memberRepository, JpaMemberRepository jpaMemberRepository, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.redisUtil = redisUtil;
        this.tokenProvider = tokenProvider;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.jpaMemberRepository = jpaMemberRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Transactional
    public String createAccount(MemberRegistrationDTO dto) {
        /* 이메일 중복 확인 */
        jpaMemberRepository.findByEmail(dto.getEmail())
                .ifPresent(member -> {
                    throw new MemberEmailDuplicationException("중복되는 이메일이 존재합니다.");
                });

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
        Member member = jpaMemberRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new EmailIncorrectException("이메일 주소가 올바르지 않습니다."));

        /* 비밀번호 일치 확인 */
        if(!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        /* 이메일 & 비밀번호를 바탕으로 인증(Authentication) 정보 생성 */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(
                        member.getEmail(), member.getPassword()
                )
        );

        /* JWT 발급 */
        return tokenProvider.createToken(authentication);
    }

    @Transactional
    public String sendAuthenticationCode(EmailAddressDTO dto) throws MessagingException, UnsupportedEncodingException {
        /* 인증코드 유효기간 5분으로 설정 */
        String code = createRandomCode();
        redisUtil.setAuthenticationExpire(dto.getEmail(), code, 5L);

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

        /* 회원가입 시 등록한 이메일로 인증코드 발송 */
        mailService.sendMail(data);
        return "SUCCESS";
    }

    @Transactional
    public String emailAuthentication(EmailAuthenticationDTO dto) {
        /* 이메일(회원) 존재 확인 */
        Member member = jpaMemberRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
        /* 회원 이메일로 전송된 인증코드 */
        String code = redisUtil.getAuthenticationCode(member.getEmail());

        /* 인증코드가 만료됐는지 확인 */
        if(code == null) {
            throw new AuthenticationCodeExpirationException("인증코드의 유효시간이 만료됐습니다.");
        }
        /* 입력한 인증코드와 발송된 인증코드 값 비교 */
        if(!code.equals(dto.getCode())) {
            throw new AuthenticationCodeMismatchException("이메일 인증코드가 일치하지 않습니다.");
        }

        /* 이메일 인증 완료 처리 */
        redisUtil.setAuthenticationComplete(member.getEmail());
        return "SUCCESS";
    }

    @Transactional
    public String passwordReset(PasswordResetDTO dto) {
        /* 이메일(회원) 존재 확인 */
        Member member = jpaMemberRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        /* 인증코드가 만료됐는지 확인 */
        if(redisUtil.getAuthenticationCode(member.getEmail()) == null) {
            throw new AuthenticationCodeExpirationException("인증코드의 유효시간이 만료됐습니다.");
        }

        /* 이메일 인증여부 확안 */
        if(!redisUtil.checkAuthentication(member.getEmail()).equals("Y")) {
            throw new AuthenticationNotCompletedException("이메일 인증이 완료되지 않았습니다.");
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