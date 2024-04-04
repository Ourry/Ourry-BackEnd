package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReplyRegistrationDTO {
    @NotBlank
    private String comment;

    @NotBlank
    @JsonSetter("solutionId")
    private Long solutionId;
}