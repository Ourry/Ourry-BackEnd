package com.bluewhaletech.Ourry.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmRequestDTO {
    private String targetToken;
    private String title;
    private String body;
}