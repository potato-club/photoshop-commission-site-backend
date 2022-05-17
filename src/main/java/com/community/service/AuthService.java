package com.community.service;

import com.community.controller.config.exception.ResourceNotFoundException;
import com.community.controller.config.oauth.JwtTokenProvider;
import com.community.dto.CustomResponse;
import com.community.dto.LoginUser;
import com.community.entity.User;
import com.nimbusds.oauth2.sdk.AccessTokenResponse;
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
//    private RedisUtil redisUtil;

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
     * refresh Token 으로 Access Token 이 만료 되었을 경우 재발급
     * Redis Server 에서 refresh Token 을 가져옴
     */
//    public AccessTokenResponse accessTokenByRefreshToken(String accessToken, RefreshTokenRequest refreshTokenRequest) {
//        refreshTokenExtractor(refreshTokenRequest);
//        String id = jwtTokenProvider.getPayload(accessToken);
//        String data = redisUtil.getData(id);
//        log.info("id = {}", id);
//        log.info("data = {}", data);
//        if (!data.equals(refreshTokenRequest.getRefreshToken())) {
//            log.info("Exception!!");
//            throw new ResourceNotFoundException("UNAUTHORIZED_REFRESH_TOKEN");
//        }
//
//        String newAccessToken = jwtTokenProvider.createAccessToken(id);
//        log.info("newAccessToken = {}", newAccessToken);
//
//        return new AccessTokenResponse(newAccessToken.getValue());
//    }

    /**
     * 로그아웃 시 토큰도 같이 삭제
     */
//    @Transactional
//    public CustomResponse logout(String accessToken) {
//        String id = jwtTokenProvider.getPayload(accessToken);
//        redisUtil.deleteData(id);
//        return new CustomResponse("로그아웃이 완료 되었습니다.");
//    }
//
//
    /**
     * AccessToken 검증 메서드
     */
    private void accessTokenExtractor(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new ResourceNotFoundException("UNAUTHORIZED_ACCESS_TOKEN");
        }
    }
//
//    /**
//     * RefreshToken 검증 메서드
//     */
//    private void refreshTokenExtractor(RefreshTokenRequest refreshTokenRequest) {
//        if (!jwtTokenProvider.validateToken(refreshTokenRequest.getRefreshToken())) {
//            throw new ResourceNotFoundException("UNAUTHORIZED_REFRESH_TOKEN");
//        }
//    }

}
