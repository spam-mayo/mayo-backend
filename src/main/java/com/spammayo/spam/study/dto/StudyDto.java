package com.spammayo.spam.study.dto;

import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.entity.StudyUser;
import lombok.AllArgsConstructor;
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

        @NotBlank(message = "스터디명은 공란일 수 없습니다.")
        private String studyName;

        @NotBlank(message = "스터디 제목은 공란일 수 없습니다.")
        private String title;

        @NotBlank(message = "시작일자는 공란일 수 없습니다.")
        private String startDate;

        @NotBlank(message = "종료일자는 공란일 수 없습니다.")
        private String endDate;

        @NotBlank(message = "모집인원은 공란일 수 없습니다.")
        private String personnel;

        private boolean online;
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

        @NotBlank(message = "스터디명은 공란일 수 없습니다.")
        private String studyName;

        @NotBlank(message = "시작일자는 공란일 수 없습니다.")
        private String startDate;

        @NotBlank(message = "종료일자는 공란일 수 없습니다.")
        private String endDate;

        @NotBlank(message = "모집인원은 공란일 수 없습니다.")
        private String personnel;

        private boolean online;
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
        private boolean online;
        private boolean checkLikes;
        private List<StackDto> stack;

        private OwnerDto owner;

        public String getStudyStatus() {
            return studyStatus.getStatus();
        }
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class ListResponseDto {
        private Long studyId;
        private String title;
        private String startDate;
        private String endDate;
        private Study.StudyStatus studyStatus;
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
        private String field;
        private String userProfileUrl;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class NoticeDto {
        private long studyId;
        @NotBlank
        private String noticeTitle;
        @NotBlank
        private String noticeContent;
    }

    @NoArgsConstructor
    @Getter @Setter
    public static class MyPageResponseDto {
        private Long studyId;
        private String title;
        private String startDate;
        private String endDate;
        private Study.StudyStatus studyStatus;
        private List<StackDto> stack;
        private StudyUser.ApprovalStatus approvalStatus;

        //admin
        private boolean isAdmin;

        public String getStudyStatus() {
            return studyStatus.getStatus();
        }

        public String getApprovalStatus() {
            return approvalStatus.getStatus();
        }
    }
}
