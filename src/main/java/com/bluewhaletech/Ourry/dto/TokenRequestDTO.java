package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenRequestDTO {
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
}