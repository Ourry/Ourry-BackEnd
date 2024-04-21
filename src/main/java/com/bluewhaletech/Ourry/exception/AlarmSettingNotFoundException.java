package com.bluewhaletech.Ourry.exception;

public class AlarmSettingNotFoundException extends ArticleException {
    public AlarmSettingNotFoundException(String message) {
        super(ErrorCode.ALARM_SETTING_NOT_FOUND, message);
    }
}