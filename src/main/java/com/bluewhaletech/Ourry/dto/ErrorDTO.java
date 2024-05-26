package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDTO {
    @NotBlank
    private String code;
    @NotBlank
    private String message;
    @NotBlank
    private int status;
}