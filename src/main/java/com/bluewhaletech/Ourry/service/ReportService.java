package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.ReportRegistrationDTO;

public interface ReportService {
    void addReport(String accessToken, ReportRegistrationDTO dto);
}