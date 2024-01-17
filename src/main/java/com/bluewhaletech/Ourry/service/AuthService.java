package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.domain.Member;
import com.bluewhaletech.Ourry.dto.JwtDTO;

public interface AuthService {
    JwtDTO issueToken(Member member) throws Exception;
}