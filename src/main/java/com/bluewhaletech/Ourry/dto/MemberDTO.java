package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Builder
public class MemberDTO {
    @NotEmpty
    @JsonSetter("memberId")
    private Long memberId;

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    @NotEmpty
    private String nickname;

    @NotEmpty
    private String phone;

    @NotEmpty
    @JsonSetter("createdAt")
    private Date createdAt;

    @NotEmpty
    @JsonSetter("updatedAt")
    private Date updatedAt;
}