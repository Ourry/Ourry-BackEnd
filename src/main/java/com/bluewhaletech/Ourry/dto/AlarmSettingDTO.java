package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class AlarmSettingDTO {
    @NotEmpty
    @JsonSetter("questionId")
    private Long questionId;

    @NotEmpty
    @JsonSetter("alarmYN")
    private String alarmYN;
}