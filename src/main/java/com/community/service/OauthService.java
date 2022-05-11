package com.community.service;

import com.community.controller.config.oauth.JwtTokenProvider;
import com.community.controller.config.oauth.Oauth2UserInfo;
import com.community.controller.config.oauth.OauthTokenResponse;
import com.community.dto.CustomResponse;
import com.community.dto.KakaoUserInfo;
import com.community.dto.LoginResponse;
import com.community.entity.User;
import com.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OauthService {
    private static final String BEARER_TYPE = "Bearer";

    private InMemoryClientRegistrationRepository inMemoryRepository;
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(String providerName, String code) {
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);
        OauthTokenResponse tokenResponse = getToken(code, provider);
        User user = getUserProfile(providerName, tokenResponse, provider);

        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtTokenProvider.createRefreshToken();

        return LoginResponse.builder()
                .id(user.getId())
                .name(user.getUserProfile().getNickName())
                .email(user.getEmail())
                .imageUrl(user.getUserProfile().getImageUrl())
                .role(user.getRole())
                .tokenType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

//    @Transactional
//    public CustomResponse logout(String accessToken) {
//        String id = jwtTokenProvider.getPayload(accessToken);
//        // redisUtil.deleteData(id);
//        return new CustomResponse("로그아웃이 완료 되었습니다.");
//    }

    private OauthTokenResponse getToken(String code, ClientRegistration provider) {
        return WebClient.create()
                .post()
                .uri(provider.getProviderDetails().getTokenUri())
                .headers(header -> {
                    header.setBasicAuth(provider.getClientId(), provider.getClientSecret());
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequest(code, provider))
                .retrieve()
                .bodyToMono(OauthTokenResponse.class)
                .block();
    }

    private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUri());
        formData.add("client_secret", provider.getClientSecret());
        formData.add("client_id", provider.getClientId());
        return formData;
    }

    private User getUserProfile(String providerName, OauthTokenResponse tokenResponse,
                                ClientRegistration provider) {
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenResponse);
        Oauth2UserInfo oauth2UserInfo = null;

        if (providerName.equals("kakao")) {
            oauth2UserInfo = new KakaoUserInfo(userAttributes);
        } else {
            log.info("허용되지 않은 접근입니다.");
        }

        String provide = oauth2UserInfo.getProvider();
        String providerId = oauth2UserInfo.getProviderId();
        String nickname = oauth2UserInfo.getNickName();
        String email = oauth2UserInfo.getEmail();
        String imageUrl = oauth2UserInfo.getImageUrl();

        User userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            userEntity = User.createUser(email, nickname, provide, providerId, imageUrl);
            userRepository.save(userEntity);
        }

        return userEntity;
    }

    // OAuth 서버에서 유저 정보 map으로 가져오기
    private Map<String, Object> getUserAttributes(ClientRegistration provider, OauthTokenResponse tokenResponse) {
        return WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header -> header.setBearerAuth(tokenResponse.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
