package com.bluewhaletech.Ourry.exception;

public class ChoiceNotFoundException extends ArticleException {
    public ChoiceNotFoundException(String message) {
        super(ErrorCode.CHOICE_NOT_FOUND, message);
    }
}
