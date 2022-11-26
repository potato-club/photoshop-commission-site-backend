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
    public String validateAndReissueToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (!jwtTokenProvider.validateToken(accessToken) && refreshToken != null) {
            log.info("validateAndReissueToken 확인");
            boolean validateRefreshToken = jwtTokenProvider.validateToken(refreshToken);
            boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(refreshToken);

            if (validateRefreshToken && isRefreshToken) {
                String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken);
                jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
                return newAccessToken;
            }
        } else {
            throw new JwtException("다시 로그인 해주세요.");
        }

        return accessToken;
    }

    @Transactional
    public boolean checkWriter(CheckEnumRequest requestDto, HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("authorization") == null) {
            return false;
        }

        String accessToken = validateAndReissueToken(request, response);

        String email = jwtTokenProvider.getUserEmail(accessToken);
        String nickname = boardRepository.getById(requestDto.getId()).getNickname();

        if (userRepository.getByEmail(email).getNickname().equals(nickname)) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public UserRole checkEnum(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("authorization") == null) {
            return GUEST;
        }

        String accessToken = validateAndReissueToken(request, response);

        String email = jwtTokenProvider.getUserEmail(accessToken);
        UserRole userEnum = userRepository.getByEmail(email).getUserRole();

        return userEnum;
    }
}
