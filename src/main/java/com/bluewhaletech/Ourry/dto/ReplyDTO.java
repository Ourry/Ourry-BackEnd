package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReplyDTO {
    @NotBlank
    private String comment;

    @NotBlank
    private String nickname;

    @NotBlank
    @JsonSetter("createdAt")
    private LocalDateTime createdAt;
}