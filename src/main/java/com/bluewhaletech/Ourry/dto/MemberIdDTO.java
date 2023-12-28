package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberIdDTO {
    @NotEmpty
    @JsonSetter("memberId")
    private Long memberId;
}
