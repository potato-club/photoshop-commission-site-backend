package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.JwtDto.CheckEnumRequest;
import com.community.site.enumcustom.UserRole;
import com.community.site.jwt.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.community.site.enumcustom.UserRole.GUEST;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

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

    @Transactional
    public boolean checkWriter(CheckEnumRequest requestDto, HttpServletRequest request) {
        if (request.getHeader("authorization") == null) {
            return false;
        }

        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {
            throw new JwtException("토큰 재발급 필요!!");
        }

        String email = jwtTokenProvider.getUserEmail(accessToken);
        String nickname = boardRepository.getById(requestDto.getId()).getNickname();

        if (userRepository.getByEmail(email).getNickname().equals(nickname)) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public UserRole checkEnum(HttpServletRequest request) {
        if (request.getHeader("authorization") == null) {
            return GUEST;
        }

        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {
            throw new JwtException("토큰 재발급 필요!!");
        }

        String email = jwtTokenProvider.getUserEmail(accessToken);
        UserRole userEnum = userRepository.getByEmail(email).getUserRole();

        return userEnum;
    }
}
