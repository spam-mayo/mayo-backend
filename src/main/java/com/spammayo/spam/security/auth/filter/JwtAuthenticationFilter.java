package com.spammayo.spam.security.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.spammayo.spam.exception.ErrorResponse;
import com.spammayo.spam.exception.ExceptionCode;
import com.spammayo.spam.security.auth.jwt.JwtTokenizer;
import com.spammayo.spam.security.dto.LoginDto;
import com.spammayo.spam.security.utils.RedisUtils;
import com.spammayo.spam.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;
    private final RedisUtils redisUtils;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginDto loginDto = new LoginDto();
        UsernamePasswordAuthenticationToken authenticationToken;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);

        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            authenticationToken
                    = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        }

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();

        String accessToken = delegateAccessToken(user);
        String refreshToken = delegateRefreshToken(user);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh", refreshToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"" + "userId" + "\":" + user.getUserId() + "}"
        );

        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }

    private String delegateAccessToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("username", user.getEmail());
        claims.put("roles", user.getRoles());

        String subject = user.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
        String encodeBase64SecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        return jwtTokenizer.generateAccessToken(claims, subject, expiration, encodeBase64SecretKey);
    }

    private String delegateRefreshToken(User user) {
        String subject = user.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
        String encodeBase64SecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());
        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, encodeBase64SecretKey);

        //refresh 발급시 redis에 저장
        redisUtils.set("refresh_" + user.getEmail(), refreshToken, jwtTokenizer.getRefreshTokenExpirationMinutes());

        return refreshToken;
    }
}
