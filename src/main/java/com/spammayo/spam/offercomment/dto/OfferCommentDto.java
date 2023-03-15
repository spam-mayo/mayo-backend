package com.spammayo.spam.offercomment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class OfferCommentDto {
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
        private Long offerCommentId;
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
        private Long offerCommentId;
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
