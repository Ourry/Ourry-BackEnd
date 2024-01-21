package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.JwtDTO;
import com.bluewhaletech.Ourry.dto.MemberLoginDTO;
import com.bluewhaletech.Ourry.dto.MemberRegistrationDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {
    String createAccount(MemberRegistrationDTO dto) throws Exception;

    String memberLogin(MemberLoginDTO dto, HttpServletResponse response) throws Exception;
}