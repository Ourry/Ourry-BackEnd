package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
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

@Controller
public class MemberController {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REFRESH_HEADER = "Refresh";

    private final JwtProvider tokenProvider;
    private final MemberServiceImpl memberService;

    @Autowired
    public MemberController(JwtProvider tokenProvider, MemberServiceImpl memberService) {
        this.tokenProvider = tokenProvider;
        this.memberService = memberService;
    }

    /**
     * 회원가입 API
     * @param dto
     * @return memberId (회원 엔티티 PK)
     */
    @PostMapping("/member/createAccount")
    public ResponseEntity<Object> createAccount(@RequestBody MemberRegistrationDTO dto) {
        memberService.createAccount(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 로그인 API
     * @param dto
     * @return jwt (JWT)
     */
    @PostMapping("/member/memberLogin")
    public ResponseEntity<Object> memberLogin(@RequestBody MemberLoginDTO dto, HttpServletResponse response) {
        JwtDTO newJwt = memberService.memberLogin(dto);
        tokenProvider.setResponseHeader(response, AUTHORIZATION_HEADER, newJwt.getAccessToken());
        tokenProvider.setResponseHeader(response, REFRESH_HEADER, newJwt.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    /**
     * JWT Token 재발급 API
     * @param request, response
     * @return
     */
    @PostMapping("/member/reissueToken")
    public ResponseEntity<Object> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(REFRESH_HEADER);
        JwtDTO newJwt = memberService.reissueToken(token.substring(7));
        tokenProvider.setResponseHeader(response, AUTHORIZATION_HEADER, newJwt.getAccessToken());
        tokenProvider.setResponseHeader(response, REFRESH_HEADER, newJwt.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 로그아웃 API
     * @param request
     * @return
     */
    @PostMapping("/member/memberLogout")
    public ResponseEntity<Object> memberLogout(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        memberService.memberLogout(token.substring(7));
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

    @PostMapping("/member/updateProfile")
    public ResponseEntity<Object> updateProfile(@RequestBody MemberDTO dto) {
        memberService.updateProfile(dto);
        return ResponseEntity.ok().build();
    }
}