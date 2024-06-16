package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReportRegistrationDTO {
    @NotBlank
    @JsonSetter("articleTypeId")
    private int articleTypeId;
    @NotBlank
    @JsonSetter("articleId")
    private Long articleId;
    @NotBlank
    @JsonSetter("categoryId")
    private int categoryId;
    @NotBlank
    private String reason;
    @NotBlank
    @JsonSetter("targetEmail")
    private String targetEmail;
}