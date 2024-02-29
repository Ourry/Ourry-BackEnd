package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReplyDTO {
    @NotBlank
    @JsonSetter("replyId")
    private Long replyId;

    @NotBlank
    private String comment;

    @NotBlank
    private Long seq;
}