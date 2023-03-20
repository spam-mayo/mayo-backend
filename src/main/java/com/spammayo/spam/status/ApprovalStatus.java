package com.spammayo.spam.status;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import lombok.Getter;

import java.util.Arrays;

public enum ApprovalStatus {
    WAITING("대기중"),
    APPROVAL("승인"),
    REJECT("거절");

    @Getter
    private final String status;

    ApprovalStatus(String status) {
        this.status = status;
    }

    public static ApprovalStatus toApprovalStatus(String data) {
        return Arrays.stream(values())
                .filter(status -> data.trim().toUpperCase().equals(status.toString()))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_VALUES));
    }
}
