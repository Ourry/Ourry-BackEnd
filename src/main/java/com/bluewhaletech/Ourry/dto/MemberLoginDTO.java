package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class MemberLoginDTO {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
