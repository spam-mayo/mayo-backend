package com.spammayo.spam.offerreply.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class OfferReplyDto {

    @Getter @Setter
    @NoArgsConstructor
    public static class PostDto {

        private Long offerCommentId;
        @NotBlank
        private String offerReply;
        private Boolean secret;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class PatchDto {

        private Long replyId;
        private Long offerCommentId;
        private Long userId;
        @NotBlank
        private String offerReply;
        private Boolean secret;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ResponseDto {
        private Long replyId;
        private Long userId;
        private String userName;
        private String profileUrl;
        private LocalDateTime createdAt;
        private String offerReply;
        private Boolean secret;
    }
}
