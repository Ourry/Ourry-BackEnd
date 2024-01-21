package com.bluewhaletech.Ourry.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {
    // Member
    EMAIL_DUPLICATION("M001", "중복되는 이메일이 존재합니다.", 200),
    EMAIL_INCORRECT("M002", "이메일 주소가 올바르지 않습니다.", 200),
    PASSWORD_MISMATCH("M003", "비밀번호가 일치하지 않습니다.", 200),
    MEMBER_NOT_FOUND("M004", "존재하지 않는 회원입니다.", 200),
    AUTHENTICATION_NOT_COMPLETED("M005", "이메일 인증이 완료되지 않았습니다.", 200),
    AUTHENTICATION_CODE_MISMATCH("M006", "인증코드가 일치하지 않습니다.", 200),
    AUTHENTICATION_CODE_EXPIRATION("M007", "인증코드의 유효시간이 만료됐습니다.", 200),
    AUTHORIZATION_NOT_FOUND("M008", "권한 정보가 존재하지 않습니다.", 200),
    JWT_TOKEN_NOT_EXPIRED("M009", "JWT 토큰이 만료되지 않았습니다.", 200),
    JWT_TOKEN_NOT_FOUND("M010", "JWT 토큰이 존재하지 않습니다.", 200),
    JWT_TOKEN_MISMATCH("M011", "JWT 토큰 정보가 일치하지 않습니다", 200),

    // 400
    INVALID_INPUT_VALUE("E400", "잘못된 요청 양식입니다.", 400),
    // 404
    NOT_FOUND("E404", "해당 페이지를 찾을 수 없습니다.", 400),

    // 500
    INTERNAL_SERVER_ERROR("E500", "서버에 오류가 발생했습니다.", 500);

    private final String code;
    private final String message;
    private final int status;
}