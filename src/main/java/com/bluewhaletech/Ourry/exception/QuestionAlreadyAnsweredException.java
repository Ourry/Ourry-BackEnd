package com.bluewhaletech.Ourry.exception;

public class QuestionAlreadyAnsweredException extends ArticleException {
    public QuestionAlreadyAnsweredException(String message) {
        super(ErrorCode.QUESTION_ALREADY_ANSWERED, message);
    }
}