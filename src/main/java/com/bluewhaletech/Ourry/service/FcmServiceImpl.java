package com.bluewhaletech.Ourry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FcmServiceImpl implements FcmService {
    private final String secretKey;
    private final String apiUrl;

    @Autowired
    public FcmServiceImpl(@Value("${fcm.secretKey}") String secretKey, @Value("${fcm.apiUrl}") String apiUrl) {
        this.secretKey = secretKey;
        this.apiUrl = apiUrl;
    }
}