package com.spammayo.spam.status;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import lombok.Getter;

import java.util.Arrays;

public enum Period {
    MONTH("매 월"),
    WEEK("매 주"),
    WORKDAYS("매 평일"),
    WEEKEND("매 주말"),
    DAILY("매일"),
    ETC("기타");

    @Getter
    private final String period;

    Period(String period) {
        this.period = period;
    }

    public static Period toPeriod(String data) {
        return Arrays.stream(values())
                .filter(period -> data.trim().toUpperCase().equals(period.toString()))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_VALUES));
    }
}
