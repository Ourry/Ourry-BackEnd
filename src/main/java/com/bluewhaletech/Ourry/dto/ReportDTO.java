package com.bluewhaletech.Ourry.dto;

import com.bluewhaletech.Ourry.domain.ArticleType;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportDTO {
    @NotBlank
    @JsonSetter("articleType")
    private ArticleType articleType;
    @NotBlank
    @JsonSetter("articleId")
    private Long articleId;
    @NotBlank
    private String status;
    @NotBlank
    private String targetEmail;
    @NotBlank
    private String targetNickname;
    @NotBlank
    @JsonSetter("reportCnt")
    private int reportCnt;
    @NotBlank
    @JsonSetter("createdAt")
    private LocalDateTime createdAt;
}