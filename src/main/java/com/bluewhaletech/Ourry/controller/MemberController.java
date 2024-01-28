package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.*;
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
    public ResponseEntity<SuccessResponse> createAccount(@RequestBody MemberRegistrationDTO dto) {
        String result = memberService.createAccount(dto); // 회원가입 성공 시 반환
        SuccessResponse data = SuccessResponse.builder()
                .result(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    /**
     * 로그인 API
     * @param dto
     * @return jwt (JWT)
     */
    @PostMapping("/member/memberLogin")
    public ResponseEntity<SuccessResponse> memberLogin(@RequestBody MemberLoginDTO dto, HttpServletResponse response) {
        SuccessResponse data = SuccessResponse.builder()
                .result(memberService.memberLogin(dto, response))
                .build();
        return ResponseEntity.ok().body(data);
    }

    /**
     * JWT Token 재발급 API
     * @param request, response
     * @return
     */
    @PostMapping("/member/reissueToken")
    public ResponseEntity<SuccessResponse> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        SuccessResponse data = SuccessResponse.builder()
                .result(memberService.reissueToken(request, response))
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