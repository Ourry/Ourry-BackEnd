package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailAuthenticationDTO {
    @NotEmpty
    private String email;
    @NotEmpty
    private String code;
}