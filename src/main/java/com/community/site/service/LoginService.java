package com.community.site.service;

import com.community.site.dto.UserDto.UserMyPageRequestDto;
import com.community.site.dto.UserDto.UserRequestDto;
import com.community.site.dto.UserDto.UserResponseDto;
import com.community.site.error.ErrorCode;
import com.community.site.error.exception.UnAuthorizedException;
import com.community.site.service.Jwt.RedisService;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.JwtDto.TokenResponse;
import com.community.site.entity.User;
import com.community.site.enumcustom.UserRole;
import com.community.site.jwt.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Random;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final KakaoAPI kakaoAPI;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final TokenService tokenService;

    @Transactional
    public MultiValueMap<String, Object> signUp(UserRequestDto userDto, HttpServletResponse response) {

        if (!userRepository.existsByNickname(userDto.getSerialCode())) {
            throw new UnAuthorizedException("식별코드가 일치하지 않습니다.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        } else if (userRepository.existsByNickname(userDto.getNickname())) {
            throw new UnAuthorizedException("중복된 닉네임입니다.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        MultiValueMap<String, Object> sessionCarrier = new LinkedMultiValueMap<>();

        User user = userRepository.findByNickname(userDto.getSerialCode());
        user.update(userDto);

        user = userRepository.findByNickname(userDto.getNickname());

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRoles());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRoles());

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        redisService.setValues(refreshToken, user.getEmail());
        sessionCarrier.add("nickname", user.getNickname());
        sessionCarrier.add("userRole", user.getUserRole());

        return sessionCarrier;
    }

    @Transactional
    public MultiValueMap<String, Object> checkUser(String code, HttpServletResponse response) {
        String access_token = kakaoAPI.getAccessToken(code);
        HashMap<String, Object> userInfo = kakaoAPI.getUserInfo(access_token);

        MultiValueMap<String, Object> sessionCarrier = new LinkedMultiValueMap<>();
        String email = userInfo.get("email").toString();

        if (userRepository.existsByEmail(email)) {

            User user = userRepository.findByEmail(email).orElseThrow(() ->
                { throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION); });

            if (user.getIntroduction().equals("")) {
                userRepository.delete(user);
                sessionCarrier.add("fail", true);
            } else {

                String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRoles());
                String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRoles());

                jwtTokenProvider.setHeaderAccessToken(response, accessToken);
                jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

                redisService.setValues(refreshToken, user.getEmail());

                sessionCarrier.add("nickname", user.getNickname());
                sessionCarrier.add("userRole", user.getUserRole());
            }
        } else {
            Random random = new Random();
            int checkNum = random.nextInt(888888) + 111111;
            String nickname = "ID" + checkNum;

            User userDto = User.builder()
                    .nickname(nickname)
                    .email(email)
                    .introduction("")
                    .userRole(UserRole.USER)
                    .build();

            userRepository.save(userDto);
            sessionCarrier.add("SerialCode", nickname);
        }
        return sessionCarrier;
    }

    @Transactional
    public boolean checkNickname(String nickname) {
        boolean nicknameDuplicate = userRepository.existsByNickname(nickname);
        return !nicknameDuplicate;
    }

    @Transactional
    public TokenResponse createToken(UserRequestDto userRequestDto, HttpServletResponse response) {
        // test code
        if (userRepository.existsByNickname(userRequestDto.getNickname())) {
            throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        User user = User.builder()
                .email("evan39@gmail.com")
                .introduction(userRequestDto.getIntroduction())
                .userRole(userRequestDto.getUserRole())
                .nickname(userRequestDto.getNickname())
                .build();

        userRepository.save(user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRoles());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRoles());

        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        redisService.setValues(refreshToken, user.getEmail());

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return tokenResponse;
    }

    @Transactional
    public String resolverToken(UserMyPageRequestDto requestDto, HttpServletRequest request,
                                HttpServletResponse response) {

        // test code
        String authorization = tokenService.validateAndReissueToken(request, response);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        String email = jwtTokenProvider.getUserEmail(authorization);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION); });

        UserRequestDto updateUser = UserRequestDto.builder()
                .nickname(requestDto.getNickname())
                .userRole(requestDto.getUserRole())
                .introduction(requestDto.getIntroduction())
                .build();

        user.update(updateUser);
        jwtTokenProvider.setHeaderAccessToken(response, authorization);
        log.info(response.getHeader("authorization"));
        return "내 정보 업데이트 완료";
    }
}
