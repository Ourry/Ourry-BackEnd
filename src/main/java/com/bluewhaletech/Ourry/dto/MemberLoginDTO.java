package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class MemberLoginDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
