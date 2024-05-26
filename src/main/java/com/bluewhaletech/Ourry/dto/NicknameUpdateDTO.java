package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NicknameUpdateDTO {
    @NotBlank
    private String nickname;
    @NotBlank
    private String password;
}