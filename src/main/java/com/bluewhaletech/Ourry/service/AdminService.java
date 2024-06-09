package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.MemberRegistrationDTO;

public interface AdminService {
    void createAdmin(MemberRegistrationDTO dto, String fcmToken) throws Exception;
}