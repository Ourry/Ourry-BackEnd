package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberRegistrationDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
    @NotBlank
    private String phone;
}