package com.spammayo.spam.studycomment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

public class StudyCommentDto {

    @Getter @Setter
    @NoArgsConstructor
    public static class PostDto {

        private Long taskId;
        @NotBlank
        private String taskDate;
        @NotBlank
        private String comment;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class PatchDto {

        private Long studyCommentId;

        private Long taskId;
        @NotBlank
        private String taskDate;

        private Long userId;
        @NotBlank
        private String comment;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ResponseDto {
        private Long studyCommentId;
        private String taskDate;
        private String comment;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AllResponseDto {
        private Long userId;
        private String userName;
        private String profileUrl;
        private String createdAt;
        private Long studyCommentId;
        private String comment;
    }
}
