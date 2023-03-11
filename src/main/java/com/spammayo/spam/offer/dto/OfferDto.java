package com.spammayo.spam.offer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class OfferDto {

    @NoArgsConstructor
    @Getter @Setter
    public static class PostDto {

        @NotBlank
        private String offerIntro;

        @NotBlank
        private String offerRule;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class PatchDto {

        private long offerId;
        private String offerIntro;
        private String offerRule;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class SimpleResponseDto {
        private long offerId;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class ResponseDto {
        private long offerId;
        private String offerIntro;
        private String offerRule;
    }
}
