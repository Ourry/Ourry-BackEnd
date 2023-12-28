package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class EmailAddressDTO {
    @NotEmpty
    private String email;
}
