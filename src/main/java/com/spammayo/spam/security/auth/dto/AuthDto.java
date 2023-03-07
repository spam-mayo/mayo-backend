package com.spammayo.spam.security.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class AuthDto {

    @NoArgsConstructor
    @Getter
    public static class EmailDto {
        @Email
        private String email;
    }

    @NoArgsConstructor
    @Getter
    public static class PasswordDto {

        @NotBlank(message = "비밀번호는 공백이 아니어야 합니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&]).{8,16}", message = "비밀번호는 영문, 특수문자, 숫자 포함 8-16자 이내여야 합니다.")
        private String newPassword;
    }

    @NoArgsConstructor
    @Getter
    public static class EmailConfirmDto {
        @Email
        private String email;

        @NotBlank
        private String authCode;
    }

    @NoArgsConstructor
    @Getter
    public static class EmailQuestionDto {
        @NotBlank(message = "내용을 입력하세요.")
        private String content;
    }
}
