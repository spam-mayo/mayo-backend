package com.spammayo.spam.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CommentDto {
    @Getter @Setter
    @NoArgsConstructor
    public static class PostDto {
        private Long offerId;
        @NotBlank
        private String comment;
        private Boolean secret;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class PatchDto {
        private Long commentId;
        private Long offerId;
        private Long userId;
        @NotBlank
        private String comment;
        private Boolean secret;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ResponseDto {
        private Long commentId;
        private Long userId;
        private Long offerId;
        private String userName;
        private String profileUrl;
        private String comment;
        private Boolean secret;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}
