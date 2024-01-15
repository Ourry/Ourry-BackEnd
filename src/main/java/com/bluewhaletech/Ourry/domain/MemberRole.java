package com.bluewhaletech.Ourry.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum MemberRole {
    USER("ROLE_USER"), MEMBER("ROLE_MEMBER"), ADMIN("ROLE_ADMIN");
    private final String label;
}