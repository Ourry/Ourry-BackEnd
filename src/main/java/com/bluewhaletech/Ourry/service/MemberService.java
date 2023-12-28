package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.MemberLoginDTO;
import com.bluewhaletech.Ourry.dto.MemberRegistrationDTO;

public interface MemberService {
    Long createAccount(MemberRegistrationDTO dto) throws Exception;

    String memberLogin(MemberLoginDTO dto) throws Exception;


}