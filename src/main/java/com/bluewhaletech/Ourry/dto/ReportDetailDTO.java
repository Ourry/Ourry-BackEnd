package com.bluewhaletech.Ourry.dto;

import com.bluewhaletech.Ourry.domain.ArticleType;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReportDetailDTO {
    @NotBlank
    @JsonSetter("reportId")
    private Long reportId;
    @NotBlank
    @JsonSetter("reportCnt")
    private int reportCnt;
    @NotBlank
    @JsonSetter("articleType")
    private ArticleType articleType;
    @NotBlank
    private String content;
    @NotBlank
    @JsonSetter("targetId")
    private Long targetId;
    @NotBlank
    private String nickname;
    @NotBlank
    @JsonSetter("createdAt")
    private LocalDateTime createdAt;
}