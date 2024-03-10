package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class QuestionDetailDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String category;

    @NotBlank
    private String nickname;

    @NotBlank
    @JsonSetter("voteCnt")
    private int voteCnt;

    @NotBlank
    @JsonSetter("responseCnt")
    private int responseCnt;

    @NotBlank
    private LocalDateTime createdAt;

    @NotBlank
    private List<ChoiceDTO> choices;

    @NotBlank
    private List<SolutionDTO> solutions;

    @NotBlank
    private List<ReplyDTO> replies;
}