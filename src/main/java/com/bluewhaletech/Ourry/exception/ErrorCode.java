package com.bluewhaletech.Ourry.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {
    // 회원
    EMAIL_DUPLICATION("M001", 400), // 이메일 중복
    EMAIL_INCORRECT("M002", 400),
    PASSWORD_MISMATCH("M003", 400),
    MEMBER_NOT_FOUND("M004", 400),
    EMAIL_AUTHENTICATION_NOT_COMPLETED("M005", 400),
    EMAIL_AUTHENTICATION_CODE_MISMATCH("M006", 400),

    // 400
    BAD_REQUEST("E400", 400),
    // 404
    PAGE_NOT_FOUND("E404", 404),

    // 500
    INTERNAL_SERVER_ERROR("E500", 500);

    private final String code;
    private final int status;
}