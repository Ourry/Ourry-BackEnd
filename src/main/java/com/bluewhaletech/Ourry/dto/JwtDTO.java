package com.bluewhaletech.Ourry.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtDTO {
    private String type;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiration;
}