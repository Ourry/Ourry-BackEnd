package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDTO {
    @NotEmpty
    private String code;

    @NotEmpty
    private String message;

    @NotEmpty
    private int status;
}