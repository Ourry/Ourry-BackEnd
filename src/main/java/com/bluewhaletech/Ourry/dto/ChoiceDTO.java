package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChoiceDTO {
    @NotBlank
    @JsonSetter("choiceId")
    private Long choiceId;

    @NotBlank
    private String detail;

    @NotBlank
    private Long seq;
}