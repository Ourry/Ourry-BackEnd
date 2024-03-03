package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChoiceDTO {
    @NotBlank
    private String detail;

    @NotBlank
    private Long seq;
}