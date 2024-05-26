package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.Authentication;

@Getter
@Builder
public class AuthenticationDTO {
    @NotBlank
    @JsonSetter("tokenId")
    private Long tokenId;
    @NotBlank
    @JsonSetter("tokenName")
    private String tokenName;
    @NotBlank
    private Authentication authentication;
}