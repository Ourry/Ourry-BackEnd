package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.jwt.JwtProvider;
import com.bluewhaletech.Ourry.service.MemberServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;

@Slf4j
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
    public ResponseEntity<SuccessResponse> createAccount(@RequestBody MemberRegistrationDTO dto) {
        String result = memberService.createAccount(dto); // 회원가입 성공 시 반환
        SuccessResponse data = SuccessResponse.builder()
                .result(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    /**
     * 회원 로그인 API
     * @param dto
     * @return jwt (JWT)
     */
    @PostMapping("/member/memberLogin")
    public ResponseEntity<SuccessResponse> memberLogin(@RequestBody MemberLoginDTO dto, HttpServletResponse response) {
        JwtDTO newJwt = memberService.memberLogin(dto);
        SuccessResponse data = SuccessResponse.builder()
                .result("SUCCESS")
                .build();
        /* Response Header 안에 Access Token & Refresh Token 생성 */
        tokenProvider.setResponseHeader(response, AUTHORIZATION_HEADER, newJwt.getAccessToken());
        tokenProvider.setResponseHeader(response, REFRESH_HEADER, newJwt.getRefreshToken());
        return ResponseEntity.ok().body(data);
    }

    /**
     * JWT Token 재발급 API
     * @param request, response
     * @return
     */
    @PostMapping("/member/reissueToken")
    public ResponseEntity<SuccessResponse> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = tokenProvider.resolveToken(request, REFRESH_HEADER);
        JwtDTO newJwt = memberService.reissueToken(refreshToken);
        SuccessResponse data = SuccessResponse.builder()
                .result("SUCCESS")
                .build();
        /* Response Header 안에 새로운 Access Token & Refresh Token 갱신 */
        tokenProvider.setResponseHeader(response, AUTHORIZATION_HEADER, newJwt.getAccessToken());
        tokenProvider.setResponseHeader(response, REFRESH_HEADER, newJwt.getRefreshToken());
        return ResponseEntity.ok().body(data);
    }

    /**
     * 회원 로그아웃 API
     * @param request
     * @return
     */
    @PostMapping("/member/memberLogout")
    public ResponseEntity<SuccessResponse> memberLogout(HttpServletRequest request) {
        String accessToken = tokenProvider.resolveToken(request, AUTHORIZATION_HEADER);
        String refreshToken = tokenProvider.resolveToken(request, REFRESH_HEADER);
        String result = memberService.memberLogout(accessToken, refreshToken);
        SuccessResponse data = SuccessResponse.builder()
                .result(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    /**
     * 인증코드 발송 API
     * @param dto
     * @return code (이메일 인증 코드)
     */
    @PostMapping("/member/sendAuthenticationCode")
    public ResponseEntity<SuccessResponse> sendAuthenticationCode(@RequestBody EmailAddressDTO dto) throws MessagingException, UnsupportedEncodingException {
        String result = memberService.sendAuthenticationCode(dto);
        SuccessResponse data = SuccessResponse.builder()
                .result(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    /**
     * 인증코드 비교 API
     * @param dto
     * @return
     */
    @PostMapping("/member/emailAuthentication")
    public ResponseEntity<SuccessResponse> emailAuthentication(@RequestBody EmailAuthenticationDTO dto) {
        String result = memberService.emailAuthentication(dto);
        SuccessResponse data = SuccessResponse.builder()
                .result(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    /**
     * 비밀번호 재설정 API
     * @param dto
     * @return
     */
    @PostMapping("/member/passwordReset")
    public ResponseEntity<SuccessResponse> passwordReset(@RequestBody PasswordResetDTO dto) {
        String result = memberService.passwordReset(dto);
        SuccessResponse data = SuccessResponse.builder()
                .result(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/member/updateProfile")
    public ResponseEntity<SuccessResponse> updateProfile(@RequestBody MemberDTO dto) {
        String result = memberService.updateProfile(dto);
        SuccessResponse data = SuccessResponse.builder()
                .result(result)
                .build();
        return ResponseEntity.ok().body(data);
    }
}