package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuestionListDTO {
    @NotBlank
    @JsonSetter("questionId")
    private Long questionId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String nickname;

    @NotBlank
    @JsonSetter("pollCnt")
    private int pollCnt;

    @NotBlank
    @JsonSetter("responseCnt")
    private int responseCnt;

    @NotBlank
    @JsonSetter("createdAt")
    private LocalDateTime createdAt;
}