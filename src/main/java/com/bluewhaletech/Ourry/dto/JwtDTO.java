package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtDTO {
    @NotBlank
    private String tokenType;
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
    @NotBlank
    private Long accessTokenExpiration;
    @NotBlank
    private Long refreshTokenExpiration;
}