package com.spammayo.spam.task.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class TaskDto {

    @NoArgsConstructor
    @Getter @Setter
    public static class PostDto {
        @NotBlank
        private String TaskDate;

        @NotBlank
        private String Task;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class PatchDto {
        private long TaskId;

        @NotBlank
        private String Task;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class SimpleResponseDto {
        private long TaskId;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class ResponseDto {
        private long TaskId;
        private String TaskDate;
        private String Task;
    }

}
