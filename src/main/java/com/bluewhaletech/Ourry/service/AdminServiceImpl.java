package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.domain.MemberRole;
import com.bluewhaletech.Ourry.dto.MemberRegistrationDTO;
import com.bluewhaletech.Ourry.exception.EmailDuplicatedException;
import com.bluewhaletech.Ourry.exception.FcmTokenNotFoundException;
import com.bluewhaletech.Ourry.exception.NicknameDuplicatedException;
import com.bluewhaletech.Ourry.repository.MemberJpaRepository;
import com.bluewhaletech.Ourry.repository.MemberRepository;
import com.bluewhaletech.Ourry.util.RedisEmailAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    private final PasswordEncoder passwordEncoder;
    private final RedisEmailAuthentication redisEmailAuthentication;
    private final MemberRepository memberRepository;
    private final MemberJpaRepository memberJpaRepository;

    @Autowired
    public AdminServiceImpl(PasswordEncoder passwordEncoder, RedisEmailAuthentication redisEmailAuthentication, MemberRepository memberRepository, MemberJpaRepository memberJpaRepository) {
        this.passwordEncoder = passwordEncoder;
        this.redisEmailAuthentication = redisEmailAuthentication;
        this.memberRepository = memberRepository;
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    @Transactional
    public void createAdmin(MemberRegistrationDTO dto, String fcmToken) {
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

        /* ADMIN 회원가입 */
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .role(MemberRole.ADMIN)
                .fcmToken(fcmToken)
                .build();
        memberRepository.save(member);

        /* Redis 내부에 저장된 이메일 인증 기록 삭제 */
        redisEmailAuthentication.deleteEmailAuthenticationHistory(member.getEmail());
    }
}