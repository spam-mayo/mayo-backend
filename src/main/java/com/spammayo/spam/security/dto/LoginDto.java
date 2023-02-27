package com.spammayo.spam.security.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class LoginDto {

    @NotBlank
    @Email
    private String email;

    //TODO : 유효성 로직 필요
    private String password;
}
