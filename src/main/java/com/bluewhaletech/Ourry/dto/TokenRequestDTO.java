package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenRequestDTO {
    @NotBlank
    @JsonSetter("memberId")
    private Long memberId;
    @NotBlank
    @JsonSetter("accessToken")
    private String accessToken;
    @NotBlank
    @JsonSetter("refreshToken")
    private String refreshToken;
}