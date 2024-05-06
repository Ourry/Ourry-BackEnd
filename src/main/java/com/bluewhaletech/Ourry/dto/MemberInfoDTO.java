package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberInfoDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phone;

    @NotBlank
    @JsonSetter("createdAt")
    private LocalDateTime createdAt;
}