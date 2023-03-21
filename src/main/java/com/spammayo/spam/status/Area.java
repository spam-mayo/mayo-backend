package com.spammayo.spam.status;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import lombok.Getter;

import java.util.Arrays;

public enum Area {
    ONLINE("온라인"),
    SEOUL("서울"),
    BUSAN("부산"),
    KEONGKIDO("경기"),
    KEONSANGDO("경상"),
    JEONRADO("전라"),
    KANGWONDO("강원"),
    JEJU("제주");

    @Getter
    private final String area;

    Area(String area) {
        this.area = area;
    }

    public static Area toArea(String data) {
        return Arrays.stream(values())
                .filter(area -> data.trim().toUpperCase().equals(area.toString()))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_VALUES));
    }
}
