package com.spammayo.spam.security.auth.controller;

import com.spammayo.spam.security.auth.dto.AuthDto;
import com.spammayo.spam.security.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") @NotBlank String accessToken) {
        authService.logout(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //토큰 재발급
    @PostMapping("/token")
    public ResponseEntity reissueAccessToken(@RequestHeader("Refresh") @NotBlank String refreshToken,
                                             HttpServletResponse response) {

        String newAccessToken = authService.reissuedToken(refreshToken);
        response.setHeader("Authorization", "Bearer " + newAccessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //비밀번호 찾기시 랜덤 링크 전송
    @PostMapping("/password")
    public ResponseEntity findPassword(@RequestBody @Valid AuthDto.EmailDto emailDto) throws MessagingException {
        authService.sendEmailForPassword(emailDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //랜덤 링크를 통한 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity authPasswordUrl(@RequestParam @NotBlank String authCode,
                                          @RequestBody @Valid AuthDto.PasswordDto passwordDto) {
        authService.setNewPassword(authCode, passwordDto.getNewPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //회원가입 이메일 인증번호 전송
    @PostMapping("/email")
    public ResponseEntity authEmail(@RequestBody @Valid AuthDto.EmailDto emailDto) throws MessagingException {
        authService.sendEmailForJoin(emailDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/email/confirm")
    public ResponseEntity confirmEmail(@RequestBody @Valid AuthDto.EmailConfirmDto confirmDto) {
        authService.authorizedEmail(confirmDto.getAuthCode(), confirmDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //관리자에게 문의하기
    @PostMapping("/email/query")
    public ResponseEntity queryEmail(@RequestBody @Valid AuthDto.EmailQuestionDto questionDto) throws MessagingException {
        authService.sendEmailForQuery(questionDto.getContent());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

