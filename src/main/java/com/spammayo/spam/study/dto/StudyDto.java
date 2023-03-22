package com.spammayo.spam.study.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.status.*;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StudyDto {

    @NoArgsConstructor
    @Getter
    @Setter
    public static class InputDto {

        @NotBlank(message = "스터디명은 공란일 수 없습니다.")
        private String studyName;

        @NotBlank(message = "스터디 제목은 공란일 수 없습니다.")
        private String title;

        @NotBlank(message = "시작일자는 공란일 수 없습니다.")
        private String startDate;

        @NotBlank(message = "종료일자는 공란일 수 없습니다.")
        private String endDate;

        private Personnel personnel;

        private boolean online;
        private String place;

        private Double latitude;
        private Double longitude;
        private List<Field> activity;
        private Period period;
        private List<StackDto> studyStacks;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PatchDto {

        private long studyId;

        @NotBlank(message = "스터디명은 공란일 수 없습니다.")
        private String studyName;

        @NotBlank(message = "시작일자는 공란일 수 없습니다.")
        private String startDate;

        @NotBlank(message = "종료일자는 공란일 수 없습니다.")
        private String endDate;

        private Personnel personnel;

        private boolean online;
        private String place;

        private Double latitude;
        private Double longitude;
        private List<Field> activity;
        private Period period;
        private List<StackDto> studyStacks;

    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class SimpleResponseDto {
        private Long studyId;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseDto {
        private Long studyId;
        private String studyName;
        private String title;
        private String startDate;
        private String endDate;
        private Personnel personnel;
        private String place;
        private Double latitude;
        private Double longitude;
        private List<Field> activity;
        private Period period;
        private StudyStatus studyStatus;
        private boolean online;
        private boolean checkLikes;
        private List<StackDto> stack;
        private LocalDate createdAt;

        private OwnerDto owner;

        public String getStudyStatus() {
            return studyStatus.getStatus();
        }

        public String getPersonnel() {
            return personnel.getPeopleNumber();
        }

        public String getPeriod() {
            return period.getPeriod();
        }

        public List<String> getActivity() {
            return activity.stream().map(Field::getField).collect(toList());
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    public static class ListResponseDto {
        private Long studyId;
        private String title;
        private String startDate;
        private String endDate;
        private StudyStatus studyStatus;
        private boolean online;
        private List<StackDto> stack;
        private boolean checkLikes;

        private SimpleOwnerDto owner;

        public String getStudyStatus() {
            return studyStatus.getStatus();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class SimpleOwnerDto {
        private long userId;
        private String userName;
        private String userProfileUrl;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class OwnerDto {
        private long userId;
        private String userName;
        private String email;
        private Field field;
        private String userProfileUrl;

        public String getField() {
            return field.getField();
        }
    }

    @NoArgsConstructor
    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NoticeDto {
        private long studyId;
        @NotBlank
        private String noticeTitle;
        @NotBlank
        private String noticeContent;
    }

    @NoArgsConstructor
    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MyPageResponseDto {
        private Long studyId;
        private String title;
        private String startDate;
        private String endDate;
        private StudyStatus studyStatus;
        private List<StackDto> stack;
        private ApprovalStatus approvalStatus;

        //admin
        private boolean isAdmin;

        public String getStudyStatus() {
            return studyStatus.getStatus();
        }

        public String getApprovalStatus() {
            if(approvalStatus == null) return null;
            return approvalStatus.getStatus();
        }
    }
}
