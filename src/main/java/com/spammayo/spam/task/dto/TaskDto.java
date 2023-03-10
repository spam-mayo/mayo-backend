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
        private String taskDate;

        @NotBlank
        private String task;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class PatchDto {
        private long taskId;

        @NotBlank
        private String task;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class SimpleResponseDto {
        private long taskId;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class ResponseDto {
        private long taskId;
        private String taskDate;
        private String task;
    }
}
