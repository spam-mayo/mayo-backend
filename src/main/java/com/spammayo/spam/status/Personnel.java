package com.spammayo.spam.status;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import lombok.Getter;

import java.util.Arrays;

public enum Personnel {

    ZERO("인원 미정"),
    UNDERFOUR("4명 이하"),
    FIVETOTEN("5명 ~ 10명"),
    OVERELEVEN("11명 이상");

    @Getter
    private final String peopleNumber;

    Personnel(String peopleNumber) {
        this.peopleNumber = peopleNumber;
    }

    public static Personnel toPersonnel(String data) {
        return Arrays.stream(values())
                .filter(personnel -> data.trim().toUpperCase().equals(personnel.toString()))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_VALUES));
    }
}
