package com.spammayo.spam.stack.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class StackDto {

    @NoArgsConstructor
    @Getter @Setter
    public static class ResponseDto {
        private Long stackId;
        private String stackName;
    }
}
