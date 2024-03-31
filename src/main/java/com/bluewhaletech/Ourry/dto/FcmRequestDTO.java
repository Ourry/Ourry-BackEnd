package com.bluewhaletech.Ourry.dto;

import lombok.Getter;

@Getter
public class FcmRequestDTO {
    private String targetToken;
    private String title;
    private String body;
}