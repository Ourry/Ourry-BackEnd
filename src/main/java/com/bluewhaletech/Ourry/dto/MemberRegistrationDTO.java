package com.bluewhaletech.Ourry.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MemberRegistrationDTO {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String nickname;
    @NotEmpty
    private String phone;
}