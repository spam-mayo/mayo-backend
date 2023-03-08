package com.spammayo.spam.security.auth.oauth;

import com.spammayo.spam.security.utils.CustomAuthorityUtils;
import com.spammayo.spam.user.entity.User;
import com.spammayo.spam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserSevice extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final CustomAuthorityUtils authorityUtils;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        if(userRepository.findByEmail(attributes.getEmail()).isEmpty()) {
            log.info("### 소셜회원 신규가입 ###");
            saveUser(attributes.getEmail()
                    ,attributes.getName()
                    ,attributes.getProfileUrl()
                    ,registrationId.toUpperCase());
        }

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private void saveUser(String email,
                          String name,
                          String profileUrl,
                          String profileKey) {
        List<String> roles = authorityUtils.createRoles(email);
        User user = new User();

        user.setEmail(email);
        user.setUserName(name);
        user.setProfileUrl(profileUrl);
        user.setProfileKey(profileKey);
        user.setPassword(profileKey);
        user.setRoles(roles);
        userRepository.save(user);
    }
}
