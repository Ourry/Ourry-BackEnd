package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.exception.FcmTokenNotFoundException;
import com.bluewhaletech.Ourry.service.MemberServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Controller
public class MemberController {
    private final MemberServiceImpl memberService;

    @Autowired
    public MemberController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    /**
     * 회원가입 API
     * @param dto
     * @return memberId (회원 엔티티 PK)
     */
    @PostMapping("/member/createAccount")
    public ResponseEntity<Object> createAccount(HttpServletRequest request, @RequestBody MemberRegistrationDTO dto) {
        String fcmToken = Optional.ofNullable(request.getHeader("FirebaseCloudMessaging"))
                .orElseThrow(() -> new FcmTokenNotFoundException("FCM 토큰값이 비어있습니다."));
        memberService.createAccount(dto, fcmToken);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 로그인 API
     * @param dto
     * @return jwt (JWT)
     */
    @PostMapping("/member/memberLogin")
    public ResponseEntity<Object> memberLogin(@RequestBody MemberLoginDTO dto, HttpServletRequest request, HttpServletResponse response) {
        String fcmToken = Optional.ofNullable(request.getHeader("FirebaseCloudMessaging"))
                .orElseThrow(() -> new FcmTokenNotFoundException("FCM 토큰값이 비어있습니다."));
        JwtDTO newJwt = memberService.memberLogin(dto, fcmToken);
        response.setHeader("Authorization", "Bearer " + newJwt.getAccessToken());
        response.setHeader("Refresh", "Bearer " + newJwt.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    /**
     * JWT Token 재발급 API
     * @param request, response
     * @return
     */
    @PostMapping("/member/reissueToken")
    public ResponseEntity<Object> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        JwtDTO newJwt = memberService.reissueToken(request.getHeader("Refresh"));
        response.setHeader("Authorization", "Bearer " + newJwt.getAccessToken());
        response.setHeader("Refresh", "Bearer " + newJwt.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 로그아웃 API
     * @param request
     * @return
     */
    @PostMapping("/member/memberLogout")
    public ResponseEntity<Object> memberLogout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        memberService.memberLogout(accessToken);
        return ResponseEntity.ok().build();
    }

    /**
     * 인증코드 발송 API
     * @param dto
     * @return code (이메일 인증 코드)
     */
    @PostMapping("/member/sendAuthenticationCode")
    public ResponseEntity<Object> sendAuthenticationCode(@RequestBody EmailAddressDTO dto) throws MessagingException, UnsupportedEncodingException {
        memberService.sendAuthenticationCode(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * 인증코드 비교 API
     * @param dto
     * @return
     */
    @PostMapping("/member/emailAuthentication")
    public ResponseEntity<Object> emailAuthentication(@RequestBody EmailAuthenticationDTO dto) {
        memberService.emailAuthentication(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 재설정 API
     * @param dto
     * @return
     */
    @PostMapping("/member/passwordReset")
    public ResponseEntity<Object> passwordReset(@RequestBody PasswordResetDTO dto) {
        memberService.passwordReset(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/member/addFcmToken")
    public ResponseEntity<Object> addFcmToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        String fcmToken = request.getHeader("FirebaseCloudMessaging");
        memberService.addFcmToken(accessToken, fcmToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/member/updateProfile")
    public ResponseEntity<Object> updateProfile(@RequestBody MemberDTO dto) {
        memberService.updateProfile(dto);
        return ResponseEntity.ok().build();
    }
}