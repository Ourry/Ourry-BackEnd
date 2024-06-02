package com.bluewhaletech.Ourry.dto;

import com.bluewhaletech.Ourry.domain.ArticleType;
import com.bluewhaletech.Ourry.domain.ReportCategory;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReportRegistrationDTO {
    @NotBlank
    private Long articleId;
    @NotBlank
    private ArticleType articleType;
    @NotBlank
    private ReportCategory reportCategory;
    @NotBlank
    private String reason;
    @NotBlank
    @JsonSetter("targetId")
    private Long targetId;
}