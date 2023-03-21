package com.spammayo.spam.status;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import lombok.Getter;

import java.util.Arrays;

public enum Field {
    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    FULLSTACK("풀스택"),
    DESIGN("디자인"),
    PLAN("기획"),
    OTHER("기타");

    @Getter
    private final String field;

    Field(String field) {
        this.field = field;
    }

    public static Field toField(String data) {
        return Arrays.stream(values())
                .filter(field -> data.trim().toUpperCase().equals(field.toString()))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_VALUES));
    }
}
