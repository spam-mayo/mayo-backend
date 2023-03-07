package com.spammayo.spam.studytodo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class StudyTodoDto {

    @NoArgsConstructor
    @Getter @Setter
    public static class PostDto {
        @NotBlank
        private String todoDate;

        @NotBlank
        private String todo;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class PatchDto {
        private long studyTodoId;

        @NotBlank
        private String todo;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class SimpleResponseDto {
        private long studyTodoId;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class ResponseDto {
        private long studyTodoId;
        private String todoDate;
        private String todo;
    }

}
