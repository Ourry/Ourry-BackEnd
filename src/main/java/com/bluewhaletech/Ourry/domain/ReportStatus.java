package com.bluewhaletech.Ourry.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ReportStatus {
    RECEIVED(0, "접수"),
    UNDERWAY(1, "처리중"),
    COMPLETED(2, "처리완료");

    private final int code;
    private final String name;
}