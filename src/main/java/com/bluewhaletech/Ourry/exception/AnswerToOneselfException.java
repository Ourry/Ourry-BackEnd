package com.bluewhaletech.Ourry.exception;

public class AnswerToOneselfException extends ArticleException {
    public AnswerToOneselfException(String message) {
        super(ErrorCode.ANSWER_TO_ONESELF, message);
    }
}
