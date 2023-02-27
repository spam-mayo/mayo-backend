package com.spammayo.spam.security.auth.controller;

import com.spammayo.spam.exception.BusinessLogicException;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.security.auth.dto.AuthDto;
import com.spammayo.spam.security.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    //로그아웃
    @PostMapping("/log-out")
    public ResponseEntity logout(@RequestHeader("Authorization") @NotBlank String accessToken) {
        authService.logout(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //토큰 재발급
    @PostMapping("/token/reissue")
    public ResponseEntity reissueAccessToken(@RequestHeader("Refresh") @NotBlank String refreshToken,
                                             HttpServletResponse response) {

        String newAccessToken = authService.reissuedToken(refreshToken);
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //비밀번호 찾기시 랜덤 링크 전송
    @PostMapping("/find/password")
    public ResponseEntity findPassword(@RequestBody AuthDto.EmailDto emailDto) throws MessagingException {
        authService.sendEmailForPassword(emailDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //랜덤 링크를 통한 비밀번호 변경
    @PostMapping("/auth/password/{random-code}")
    public ResponseEntity authPasswordUrl(@PathVariable("random-code") String randomCode,
                                          @RequestBody AuthDto.PasswordDto passwordDto) {
        String firstPassword = passwordDto.getFirstPassword();
        String secondPassword = passwordDto.getSecondPassword();
        if (!firstPassword.equals(secondPassword)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_VALUES);
        }
        authService.setNewPassword(randomCode, firstPassword);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //회원가입 이메일 인증번호 전송
    @PostMapping("/auth/email")
    public ResponseEntity authEmail(@RequestBody AuthDto.EmailDto emailDto) throws MessagingException {
        authService.sendEmailForJoin(emailDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/auth/email/confirm")
    public ResponseEntity confirmEmail(@RequestBody AuthDto.EmailConfirmDto confirmDto) {
        authService.authorizedEmail(confirmDto.getAuthCode(), confirmDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

