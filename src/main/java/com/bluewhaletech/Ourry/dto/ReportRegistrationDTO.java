package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReportRegistrationDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String reason;
    @NotBlank
    @JsonSetter("articleType")
    private String articleType;
    @NotBlank
    @JsonSetter("authorId")
    private Long authorId;
    @NotBlank
    @JsonSetter("targetId")
    private Long targetId;
}