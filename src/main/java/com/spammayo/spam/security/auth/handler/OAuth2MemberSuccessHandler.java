package com.spammayo.spam.security.auth.handler;

import com.spammayo.spam.security.auth.jwt.JwtTokenizer;
import com.spammayo.spam.security.utils.RedisUtils;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OAuth2MemberSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenizer jwtTokenizer;
    private final UserRepository userRepository;
    private final RedisUtils redisUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email;
        String clientId;
        var oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakao = (Map<String, Object>) attributes.get("kakao_account");

        if (kakao != null) {
            email = kakao.get("email").toString();
            clientId = "KAKAO";
        }
        else{
            email = attributes.get("email").toString();
            clientId = "GOOGLE";
        }

        String password = userRepository.findByEmail(email).get().getPassword();

        log.info("User Email : {}", email);

        redirect(request, response, email, password.equals(clientId));
    }
    private void redirect(HttpServletRequest request,
                          HttpServletResponse response,
                          String username,
                          Boolean isMember) throws IOException {
        String accessToken = "";
        String refreshToken = "";

        if(isMember) {
            accessToken = delegateAccessToken(username, List.of("USER"));
            refreshToken = delegateRefreshToken(username);

            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("Refresh", refreshToken);
        }

        User user = userRepository.findByEmail(username).get();
        String uri = createURI("Bearer " + accessToken, refreshToken, user.getUserId()).toString();

        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private String delegateAccessToken(String username, List<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("roles", authorities);

        String subject = username;
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String accessToken = jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);

        log.info("--- 액세스 토큰 생성완료 ---");

        return accessToken;
    }

    private String delegateRefreshToken(String username) {
        String subject = username;
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        String refreshToken = jwtTokenizer.generateRefreshToken(subject, expiration, base64EncodedSecretKey);

        redisUtils.set(subject, refreshToken, jwtTokenizer.getRefreshTokenExpirationMinutes());

        return refreshToken;
    }

    private URI createURI(String accessToken, String refreshToken, Long userId) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        queryParams.add("accessToken", accessToken);
        queryParams.add("refreshToken", refreshToken);
        queryParams.add("userId", String.valueOf(userId));

        return UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host("https://mayo-frontend-47zd.vercel.app/")
                .port(80)
                .path("/loading")
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}
