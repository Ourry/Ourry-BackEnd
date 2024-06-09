package com.bluewhaletech.Ourry.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ArticleCategory {
    MY_QUESTION("내 질문"),
    STUDY_AND_CAREER("학업 & 커리어"),
    LOVE_AND_MARRIAGE("연애 & 결혼"),
    HOME_AND_PARENTING("가정 & 육아"),
    ECONOMY_AND_INVESTMENT("경제 & 재테크"),
    SOCIETY_AND_RELATIONSHIP("사회생활 & 인간관계");

    private final String name;
}