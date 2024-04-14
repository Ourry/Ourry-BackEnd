package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.dto.MemberLoginDTO;
import com.bluewhaletech.Ourry.dto.MemberRegistrationDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {
    void createAccount(MemberRegistrationDTO dto, String fcmToken) throws Exception;

    JwtDTO memberLogin(MemberLoginDTO dto) throws Exception;

    JwtDTO reissueToken(String refreshToken) throws Exception;

    void memberLogout(String accessToken) throws Exception;
}