package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SolutionDTO {
    @NotBlank
    @JsonSetter("solutionId")
    private Long solutionId;
    @NotBlank
    private int sequence;
    @NotBlank
    private String opinion;
    @NotBlank
    @JsonSetter("createdAt")
    private LocalDateTime createdAt;
    @NotBlank
    @JsonSetter("memberId")
    private Long memberId;
    @NotBlank
    private String nickname;
}