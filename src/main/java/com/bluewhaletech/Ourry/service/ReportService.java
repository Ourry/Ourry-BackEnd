package com.bluewhaletech.Ourry.service;

import com.bluewhaletech.Ourry.dto.ReportDTO;
import com.bluewhaletech.Ourry.dto.ReportRegistrationDTO;

import java.util.List;

public interface ReportService {
    List<ReportDTO> getReportList(String accessToken) throws Exception;

    void addReport(String accessToken, ReportRegistrationDTO dto) throws Exception;
}