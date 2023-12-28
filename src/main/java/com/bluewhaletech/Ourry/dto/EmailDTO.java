package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailDTO {
    @NotEmpty
    private String email;
    @NotEmpty
    private String title;
    @NotEmpty
    private String text;
}
