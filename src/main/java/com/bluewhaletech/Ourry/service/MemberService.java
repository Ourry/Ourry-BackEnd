package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.dto.MemberLoginDTO;
import com.bluewhaletech.Ourry.dto.MemberRegistrationDTO;

public interface MemberService {
    void createAccount(MemberRegistrationDTO dto, String fcmToken) throws Exception;

    JwtDTO memberLogin(MemberLoginDTO dto, String fcmToken) throws Exception;

    JwtDTO reissueToken(String refreshToken) throws Exception;

    void memberLogout(String accessToken) throws Exception;
}