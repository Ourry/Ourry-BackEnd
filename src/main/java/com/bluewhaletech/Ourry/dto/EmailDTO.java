package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String title;
    @NotBlank
    private String text;
}
