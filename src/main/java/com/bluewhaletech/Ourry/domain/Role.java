package com.bluewhaletech.Ourry.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum Role {
    USER("사용자"),
    MEMBER("회원"),
    ADMIN("관리자");

    private final String label;
}