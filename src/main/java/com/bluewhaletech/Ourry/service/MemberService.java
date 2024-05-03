package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.*;

public interface MemberService {
    MemberInfoDTO getMemberInfo(String email) throws Exception;

    void updateProfile(MemberDTO dto) throws Exception;

    void createAccount(MemberRegistrationDTO dto, String fcmToken) throws Exception;

    JwtDTO memberLogin(MemberLoginDTO dto, String fcmToken) throws Exception;

    JwtDTO reissueToken(String refreshToken) throws Exception;

    void memberLogout(String accessToken) throws Exception;

    void sendAuthenticationCode(EmailAddressDTO dto) throws Exception;

    void emailAuthentication(EmailAuthenticationDTO dto) throws Exception;

    void passwordReset(PasswordResetDTO dto) throws Exception;

    void addFcmToken(String email, String fcmToken) throws Exception;
}