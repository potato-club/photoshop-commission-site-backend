package com.community.service;

import com.community.controller.config.exception.ResourceNotFoundException;
import com.community.controller.config.oauth.JwtTokenProvider;
import com.community.dto.LoginUser;
import com.community.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private JwtTokenProvider jwtTokenProvider;
    private UserService userService;

    /**
     * access token 이 유효한지 확인
     */
    public void validateAccessToken(String accessToken) {
        accessTokenExtractor(accessToken);
    }


    /**
     * 토큰으로 회원 조회
     */
    @Transactional(readOnly = true)
    public LoginUser findUserByToken(String accessToken) {
        if (!accessToken.isEmpty()) {
            accessTokenExtractor(accessToken);
        }

        Long id = Long.parseLong(jwtTokenProvider.getPayload(accessToken));
        User findUser = userService.findById(id);
        return new LoginUser(findUser.getId());
    }

    /**
     * AccessToken 검증 메서드
     */
    private void accessTokenExtractor(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new ResourceNotFoundException("UNAUTHORIZED_ACCESS_TOKEN");
        }
    }
}
