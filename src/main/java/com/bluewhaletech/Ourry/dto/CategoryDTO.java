package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryDTO {
    @NotBlank
    @JsonSetter("categoryId")
    private Long categoryId;

    @NotBlank
    private String name;
}
