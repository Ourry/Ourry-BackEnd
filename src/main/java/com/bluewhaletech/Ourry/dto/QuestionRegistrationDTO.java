package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class QuestionRegistrationDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    @JsonSetter("categoryId")
    private Long categoryId;

    @NotBlank
    private List<ChoiceDTO> choices;
}