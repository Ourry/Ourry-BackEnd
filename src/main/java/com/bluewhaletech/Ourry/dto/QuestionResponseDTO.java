package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class QuestionResponseDTO {
    @NotBlank
    @JsonSetter("memberId")
    private Long memberId;

    @NotBlank
    @JsonSetter("choiceId")
    private Long choiceId;

    private String opinion;
}