package com.bluewhaletech.Ourry.exception;

public class CategoryNotFoundException extends CategoryException {
    public CategoryNotFoundException(String message) {
        super(ErrorCode.CATEGORY_NOT_FOUND, message);
    }
}
