package com.bluewhaletech.Ourry.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {
    // 회원
    EMAIL_DUPLICATION("M001", "중복되는 이메일이 존재합니다. 다시 확인해주세요. ", 400), // 이메일 중복
    EMAIL_INCORRECT("M002", "이메일이 일치하지 않습니다. 다시 확인해주세요", 400), // 이메일 불일치
    PASSWORD_MISMATCH("M003", "비밀번호가 일치하지 않습니다. 다시 확인해주세요.", 400), // 비밀번호 불일치
    MEMBER_NOT_FOUND("M004", "회원 정보를 찾을 수 없습니다.", 400), // 등록되지 않은 회원
    EMAIL_AUTHENTICATION_NOT_COMPLETED("M005", "이메일 인증이 완료되지 않았습니다.", 400), // 이메일 인증 미완료
    EMAIL_AUTHENTICATION_CODE_MISMATCH("M006", "이메일 인증코드가 일치하지 않습니다. 다시 확인해주세요.", 400), // 이메일 인증코드 불일치

    // 인증
    JWT_EXPIRED("A001", "JWT 토큰이 만료됐습니다. 토큰을 재발급해주세요.", 401), // 유효시간이 만료된 JWT
    JWT_MALFORMED("A002", "유효하지 않은 JWT 토큰입니다.", 401), // 유효하지 않은 JWT
    JWT_UNSUPPORTED("A003", "지원되지 않는 JWT 토큰입니다.", 401), // 지원되지 않는 JWT
    BAD_CREDENTIALS("A004", "자격 증명에 실패했습니다. 아이디 또는 비밀번호를 다시 확인해주세요.", 401), // ID 또는 PW 불일치
    ILLEGAL_ARGUMENT("A005", "올바르지 않은 JWT 토큰 형식입니다.", 401), // JWT Claims 정보 없음
    NOT_LOGGED_IN("A006", "로그인 정보가 존재하지 않습니다.", 401), // 로그인 정보 없음
    JWT_NOT_FOUND("A007", "JWT 토큰 정보가 존재하지 않습니다.", 401), // 존재하지 않는 Refresh Token
    JWT_MISMATCH("A008", "JWT 토큰 정보가 일치하지 않습니다.", 401), // Refresh Token 값 불일치

    // 400
    BAD_REQUEST("400", "잘못된 요청입니다.", 400),
    // 401
    UNAUTHORIZED("401", "권한이 없습니다.", 401),

    // 500
    INTERNAL_SERVER_ERROR("500", "서버에 문제가 발생했습니다.", 500);

    private final String code;
    private final String message;
    private final int status;
}