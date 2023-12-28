package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PasswordResetDTO {
    @NotEmpty
    private String email;
    @NotEmpty
    @JsonSetter("newPassword")
    private String newPassword;
    @NotEmpty
    @JsonSetter("confirmPassword")
    private String confirmPassword;
}