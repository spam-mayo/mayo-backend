package com.spammayo.spam.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spammayo.spam.stack.dto.StackDto;
import com.spammayo.spam.status.ApprovalStatus;
import com.spammayo.spam.status.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class UserDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class PostDto {

        @Size(min = 2, max = 16)
        @NotBlank(message = "이름은 공백이 아니어야 합니다.")
        private String userName;

        @Email
        @NotBlank(message = "이메일은 공백이 아니어야 합니다.")
        private String email;

        @NotBlank(message = "비밀번호는 공백이 아니어야 합니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&]).{8,16}", message = "비밀번호는 영문, 특수문자, 숫자 포함 8-16자 이내여야 합니다.")
        private String password;

        //optional
        private Field field;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatchDto {
        private Long userId;

        @Size(min = 2, max = 16)
        private String userName;

        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&]).{8,16}", message = "비밀번호는 영문, 특수문자, 숫자 포함 8-16자 이내여야 합니다.")
        private String password;
        private Field field;
        private List<String> userStacks;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class SimpleResponseDto {
        private Long userId;
    }

    //특정 회원 조회
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResponseDto {
        private Long userId;
        private String userName;
        private String email;
        private String profileUrl;
        private Field field;
        private List<StackDto> stack;

        public String getField() {
            if(field == null) return null;
            return field.getField();
        }
    }

    //스터디원 목록 조회
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListResponseDto {
        private Long userId;
        private String userName;
        private String profileUrl;
        private Field field;
        private String createdAt;
        private String applicationDate;
        private ApprovalStatus approvalStatus;

        public String getField() {
            if(field == null) return null;
            return field.getField();
        }

        public String getApprovalStatus() {
            return approvalStatus.getStatus();
        }
    }
}
