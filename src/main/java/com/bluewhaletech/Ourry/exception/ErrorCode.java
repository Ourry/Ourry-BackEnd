package com.bluewhaletech.Ourry.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {
    // 회원
    EMAIL_DUPLICATED("M001", "중복되는 이메일이 존재합니다. 다시 확인해주세요. ", 400), // 이메일 중복
    EMAIL_INCORRECT("M002", "이메일이 일치하지 않습니다. 다시 확인해주세요", 400), // 이메일 불일치
    PASSWORD_MISMATCH("M003", "비밀번호가 일치하지 않습니다. 다시 확인해주세요.", 400), // 비밀번호 불일치
    MEMBER_NOT_FOUND("M004", "회원 정보를 찾을 수 없습니다.", 400), // 등록되지 않은 회원
    EMAIL_AUTHENTICATION_NOT_COMPLETED("M005", "이메일 인증이 완료되지 않았습니다.", 400), // 이메일 인증 미완료
    EMAIL_AUTHENTICATION_CODE_MISMATCH("M006", "이메일 인증코드가 일치하지 않습니다. 다시 확인해주세요.", 400), // 이메일 인증코드 불일치
    NICKNAME_DUPLICATED("M008", "중복되는 닉네임이 존재합니다. 다시 확인해주세요.", 400),
    FCM_TOKEN_NOT_FOUND("M008", "FCM 토큰을 불러올 수 없습니다.", 400),

    // 인증
    EMPTY_JWT("A001", "JWT 토큰이 인입되지 않았습니다.", 401), // Access Token 없는 요청
    JWT_EXPIRED("A002", "JWT 토큰이 만료됐습니다. 토큰을 재발급해주세요.", 401), // 유효시간이 만료된 JWT
    JWT_MALFORMED("A003", "유효하지 않은 JWT 토큰입니다.", 401), // 유효하지 않은 JWT
    JWT_UNSUPPORTED("A004", "지원되지 않는 JWT 토큰입니다.", 401), // 지원되지 않는 JWT
    BAD_CREDENTIALS("A005", "자격 증명에 실패했습니다. 아이디 또는 비밀번호를 다시 확인해주세요.", 401), // ID 또는 PW 불일치
    ILLEGAL_ARGUMENT("A006", "올바르지 않은 JWT 토큰 형식입니다.", 401), // JWT Claims 정보 없음
    ALREADY_LOGGED_OUT("A007", "이미 로그아웃이 완료된 토큰입니다.", 401), // 로그아웃이 완료된 토큰
    NOT_LOGGED_IN("A008", "로그인 정보가 존재하지 않습니다.", 401), // 로그인 정보 없음
    JWT_NOT_FOUND("A008", "JWT 토큰 정보가 존재하지 않습니다.", 401), // 존재하지 않는 Refresh Token
    JWT_MISMATCH("A009", "JWT 인증 정보가 일치하지 않습니다.", 401), // Refresh Token 값 불일치

    // 카테고리
    CATEGORY_NOT_FOUND("C001", "카테고리를 찾을 수 없습니다.", 400),

    // 게시물
    QUESTION_LOADING_FAILED("Q001", "질문 목록을 불러오지 못했습니다.", 400),
    QUESTION_NOT_FOUND("Q002", "질문 정보를 찾을 수 없습니다.", 400),
    CHOICE_NOT_FOUND("Q003", "선택지 정보를 찾을 수 없습니다.", 400),
    POLL_NOT_FOUND("Q004", "투표 정보를 찾을 수 없습니다.", 400),
    SOLUTION_NOT_FOUND("Q005", "투표에 대한 의견 정보를 찾을 수 없습니다.", 400),
    QUESTION_ALREADY_ANSWERED("Q006", "이미 답변을 완료한 질문입니다.", 400),
    ANSWER_TO_ONESELF("Q007", "본인이 질문한 글에 의견을 제출했습니다.", 400),
    ALARM_SETTING_NOT_FOUND("Q008", "질문에 대한 알림 설정 기록이 존재하지 않습니다.", 400),

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