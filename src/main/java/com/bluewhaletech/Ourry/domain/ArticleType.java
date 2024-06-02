package com.bluewhaletech.Ourry.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ArticleType {
    QUESTION(0, "질문"),
    SOLUTION(1, "솔루션"),
    REPLY(2, "답글");

    private final int code;
    private final String name;
}