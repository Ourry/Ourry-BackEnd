package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.dto.MemberLoginDTO;
import com.bluewhaletech.Ourry.dto.MemberRegistrationDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {
    String createAccount(MemberRegistrationDTO dto) throws Exception;

    JwtDTO memberLogin(MemberLoginDTO dto) throws Exception;

    JwtDTO reissueToken(String refreshToken) throws Exception;

    String memberLogout(String accessToken, String refreshToken) throws Exception;
}