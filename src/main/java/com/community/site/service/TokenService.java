package com.community.site.service;

import com.community.site.jwt.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String validateToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {

            boolean validateRefreshToken = jwtTokenProvider.validateToken(refreshToken);
            boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(refreshToken);

            if (validateRefreshToken && isRefreshToken) {
                String email = jwtTokenProvider.getUserEmail(refreshToken);
                List<String> roles = jwtTokenProvider.getRoles(email);

                String newAccessToken = jwtTokenProvider.createAccessToken(email, roles);
                jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);

                return "액세스 토큰 재발급 완료";
            }
        } else {
            throw new JwtException("다시 로그인 해주세요.");
        }

        return "토큰 양호";
    }
}
