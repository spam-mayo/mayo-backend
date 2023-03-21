package com.spammayo.spam.status;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import lombok.Getter;

import java.util.Arrays;

public enum StudyStatus {

    BEFORE_RECRUITMENT("모집전"),
    RECRUITING("모집중"),
    ONGOING("진행중"),
    END("종료"),
    CLOSED("폐쇄");

    @Getter
    private String status;

    StudyStatus(String status) {
        this.status = status;
    }

    public static StudyStatus toStudyStatus(String data) {
        return Arrays.stream(values())
                .filter(status -> data.trim().toUpperCase().equals(status.toString()))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_VALUES));
    }
}