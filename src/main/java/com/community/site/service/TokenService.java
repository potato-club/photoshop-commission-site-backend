package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.entity.BoardList;
import com.community.site.entity.User;
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

    @Transactional  // 토큰 검증 및 accessToken 만료 시 재발급해주는 기능을 가지고 있다.
    public String validateAndReissueToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (accessToken == null && refreshToken == null) { return "guest"; }

        boolean isRefreshToken = jwtTokenProvider.existsRefreshToken(refreshToken);
        boolean checkAccessToken = jwtTokenProvider.validateToken(accessToken);
        boolean checkRefreshToken = jwtTokenProvider.validateToken(refreshToken);

        if (checkAccessToken && checkRefreshToken) {
            return accessToken;
        } else if (!checkAccessToken && refreshToken != null) {
            if (checkRefreshToken && isRefreshToken) {
                String newAccessToken = jwtTokenProvider.reissueAccessToken(refreshToken);
                jwtTokenProvider.setHeaderAccessToken(response, newAccessToken);
                return newAccessToken;
            }
        } else if(!checkRefreshToken) {
            throw new JwtException("다시 로그인 해주세요.");
        }

        return accessToken;
    }

    @Transactional  // 의뢰자가 맞는지 확인하는 기능이다.
    public boolean checkWriter(Long id, HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("authorization") == null) {
            return false;
        }

        String accessToken = validateAndReissueToken(request, response);

        String email = jwtTokenProvider.getUserEmail(accessToken);
        String nickname = boardRepository.getById(id).getNickname();

        if (userRepository.getByEmail(email).getNickname().equals(nickname)) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional  // 토큰으로 해당 유저의 Enum 타입을 조회할 수 있는 기능이다.
    public UserRole checkEnum(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("authorization") == null) {
            return GUEST;
        }

        String accessToken = validateAndReissueToken(request, response);

        String email = jwtTokenProvider.getUserEmail(accessToken);
        UserRole userEnum = userRepository.getByEmail(email).getUserRole();

        return userEnum;
    }

    @Transactional  // 토큰을 통해 해당 유저가 이 의뢰를 수주받은 ARTIST인지 판별하는 기능이다.
    public boolean checkSelectedArtist(Long id, HttpServletRequest request, HttpServletResponse response) {

        String accessToken = validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(accessToken);

        User user = userRepository.findByEmail(email).orElseThrow();
        BoardList boardList = boardRepository.findById(id).orElseThrow();

        if (boardList.getSelectedArtist() != null && boardList.getSelectedArtist().getNickname().equals(user.getNickname())) {
            return true;
        } else {
            return false;
        }
    }
}
