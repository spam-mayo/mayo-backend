package com.spammayo.spam.study.dto;

import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.study.entity.Study;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class StudyDto {

    @NoArgsConstructor
    @Getter
    @Setter
    public static class InputDto {

        private long studyId;

        @NotBlank
        private String studyName;

        //TODO : 타이틀 여부 확인 필요
        @NotBlank
        private String title;

        @NotBlank
        private String startDate;

        @NotBlank
        private String endDate;

        @NotBlank
        private String personnel;

        @NotBlank
        private String place;

        private String placeDetails;
        private String address;
        private String activity;
        private String period;
        private List<StackDto> studyStacks;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PatchDto {

        private long studyId;

        @NotBlank
        private String studyName;

        //TODO : 타이틀 여부 확인 필요
        @NotBlank
        private String title;

        @NotBlank
        private String startDate;

        @NotBlank
        private String endDate;

        @NotBlank
        private String personnel;

        @NotBlank
        private String place;

        private String placeDetails;
        private String address;
        private String activity;
        private String period;
        private List<StackDto> studyStacks;

    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class SimpleResponseDto {
        private Long studyId;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ResponseDto {
        private Long studyId;
        private String studyName;
        private String title;
        private String startDate;
        private String endDate;
        private String personnel;
        private String place;
        private String placeDetails;
        private String address;
        private String activity;
        private String period;
        private Study.StudyStatus studyStatus;
        private List<StackDto> stack;

        public String getStudyStatus() {
            return studyStatus.getStatus();
        }
    }
}
