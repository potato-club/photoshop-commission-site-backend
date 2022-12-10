package com.community.site.service;

import com.community.site.Repository.UserRepository;
import com.community.site.dto.UserDto.UserMyPageRequestDto;
import com.community.site.dto.UserDto.UserRequestDto;
import com.community.site.dto.UserDto.UserResponseDto;
import com.community.site.entity.User;
import com.community.site.error.ErrorCode;
import com.community.site.error.exception.UnAuthorizedException;
import com.community.site.jwt.JwtTokenProvider;
import com.community.site.service.Jwt.RedisService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class MyPageService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final TokenService tokenService;

    @Transactional
    public UserResponseDto viewMyPage(HttpServletRequest request, HttpServletResponse response) {     // 내 정보 보기
        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION); });

        UserResponseDto userResponseDto = new UserResponseDto(user);
        return userResponseDto;
    }

    @Transactional
    public void updateMyPage(UserMyPageRequestDto userDto, HttpServletRequest request,
                             HttpServletResponse response) {    // 내 정보 업데이트
        if (!jwtTokenProvider.validateToken(tokenService.validateAndReissueToken(request, response))) {
            throw new JwtException("다시 로그인 해주시길 바랍니다.");
        }

        UserRequestDto myDto = UserRequestDto.builder()
                .nickname(userDto.getNickname())
                .userRole(userDto.getUserRole())
                .introduction(userDto.getIntroduction())
                .build();

        myDto.toEntity();
    }

    @Transactional
    public void writeReviewAndGrade() {

    }

    @Transactional
    public void resign(HttpServletRequest request, HttpServletResponse response) {    // 회원 탈퇴
        String token = tokenService.validateAndReissueToken(request, response);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION); });

        userRepository.delete(user);
        redisService.delValues(refreshToken);
    }
}
