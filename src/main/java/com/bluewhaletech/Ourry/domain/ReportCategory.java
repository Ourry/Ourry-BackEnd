package com.bluewhaletech.Ourry.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ReportCategory {
    SPAMMING_ARTICLES("게시물 도배"),
    ILLEGAL_INFORMATION("불법정보 포함"),
    PERSONAL_INFORMATION("개인정보 포함"),
    OFFENSIVE_EXPRESSION("욕설/혐오 및 불쾌한 표현"),
    SPREADING_PORNOGRAPHY("음란물 유포");

    private final String name;
}