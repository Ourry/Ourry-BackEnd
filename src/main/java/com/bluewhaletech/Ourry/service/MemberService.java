package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.MemberLoginDTO;
import com.bluewhaletech.Ourry.dto.MemberRegistrationDTO;

public interface MemberService {
    String createAccount(MemberRegistrationDTO dto) throws Exception;

    String memberLogin(MemberLoginDTO dto) throws Exception;
}