package com.bluewhaletech.Ourry.controller;

import com.bluewhaletech.Ourry.dto.*;
import com.bluewhaletech.Ourry.service.MemberServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

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
    public ResponseEntity<SuccessResponseDTO> createAccount(@RequestBody MemberRegistrationDTO dto) {
        String result = memberService.createAccount(dto); // 회원가입 성공 시 반환
        SuccessResponseDTO data = SuccessResponseDTO.builder()
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
    public ResponseEntity<SuccessResponseDTO> memberLogin(@RequestBody MemberLoginDTO dto, HttpServletResponse response) {
        SuccessResponseDTO data = SuccessResponseDTO.builder()
                .result(memberService.memberLogin(dto, response))
                .build();
        return ResponseEntity.ok().body(data);
    }

    /**
     * JWT Token 재발급 API
     * @param dto
     * @return
     */
    @PostMapping("/member/reissueToken")
    public ResponseEntity<SuccessResponseDTO> reissueToken(@RequestBody TokenRequestDTO dto, HttpServletResponse response) {
        SuccessResponseDTO data = SuccessResponseDTO.builder()
                .result(memberService.reissueToken(dto, response))
                .build();
        return ResponseEntity.ok().body(data);
    }

    /**
     * 인증코드 발송 API
     * @param dto
     * @return code (이메일 인증 코드)
     */
    @PostMapping("/member/sendAuthenticationCode")
    public ResponseEntity<SuccessResponseDTO> sendAuthenticationCode(@RequestBody EmailAddressDTO dto) throws MessagingException, UnsupportedEncodingException {
        String result = memberService.sendAuthenticationCode(dto);
        SuccessResponseDTO data = SuccessResponseDTO.builder()
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
    public ResponseEntity<SuccessResponseDTO> emailAuthentication(@RequestBody EmailAuthenticationDTO dto) {
        String result = memberService.emailAuthentication(dto);
        SuccessResponseDTO data = SuccessResponseDTO.builder()
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
    public ResponseEntity<SuccessResponseDTO> passwordReset(@RequestBody PasswordResetDTO dto) {
        String result = memberService.passwordReset(dto);
        SuccessResponseDTO data = SuccessResponseDTO.builder()
                .result(result)
                .build();
        return ResponseEntity.ok().body(data);
    }
}