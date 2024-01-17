package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDTO {
    @NotBlank
    private String token;
    @NotBlank
    private Long expiration;
}
