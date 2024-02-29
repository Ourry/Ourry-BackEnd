package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuestionTotalDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String nickname;

    @NotBlank
    private int solutionCnt;

    @NotBlank
    private int responseCnt;

    @NotBlank
    private LocalDateTime createdAt;
}