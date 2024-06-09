package com.bluewhaletech.Ourry.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ReportStatus {
    RECEIVED("접수"),
    UNDERWAY("처리중"),
    COMPLETED("처리완료");

    private final String name;
}