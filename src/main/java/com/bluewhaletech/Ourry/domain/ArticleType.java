package com.bluewhaletech.Ourry.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ArticleType {
    QUESTION("질문"),
    SOLUTION("솔루션"),
    REPLY("답글");

    private final String name;
}