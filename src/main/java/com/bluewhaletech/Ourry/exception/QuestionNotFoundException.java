package com.bluewhaletech.Ourry.exception;

public class QuestionNotFoundException extends ArticleException {
    public QuestionNotFoundException(String message) {
        super(ErrorCode.QUESTION_NOT_FOUND, message);
    }
}
