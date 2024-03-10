package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PollResultDTO {
    @NotBlank
    private int sequence;

    @NotBlank
    @JsonSetter("memberId")
    private Long memberId;
}