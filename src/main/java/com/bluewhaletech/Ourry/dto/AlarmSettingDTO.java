package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class AlarmSettingDTO {
    @NotBlank
    @JsonSetter("questionId")
    private Long questionId;

    @NotBlank
    @JsonSetter("alarmYN")
    private String alarmYN;
}