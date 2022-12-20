package com.community.site.service;

import com.community.site.dto.UserDto.TestRequestDto;
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

    @Transactional  // 회원 가입 기능
    public MultiValueMap<String, Object> signUp(UserRequestDto userDto, HttpServletResponse response) {

        if (!userRepository.existsByNickname(userDto.getSerialCode())) {
            throw new UnAuthorizedException("식별코드가 일치하지 않습니다.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        } else if (userRepository.existsByNickname(userDto.getNickname())) {
            throw new UnAuthorizedException("중복된 닉네임입니다.", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        MultiValueMap<String, Object> sessionCarrier = new LinkedMultiValueMap<>();

        User user = userRepository.findByNickname(userDto.getSerialCode());
        userDto.setGrade(0.0);
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

    @Transactional  // 카카오 소셜 로그인 후 신규 회원인지 기존 회원인지 판별하는 기능이다.
    public MultiValueMap<String, Object> checkUser(String code, HttpServletResponse response) {
        String access_token = kakaoAPI.getAccessToken(code);
        HashMap<String, Object> userInfo = kakaoAPI.getUserInfo(access_token);

        MultiValueMap<String, Object> sessionCarrier = new LinkedMultiValueMap<>();
        String email = userInfo.get("email").toString();

        if (userRepository.existsByEmail(email)) {

            User user = userRepository.findByEmail(email).orElseThrow(() ->
                { throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION); });

            if (user.getIntroduction().equals("")) {    // 회원 가입 도중 나갔을 때 기존 정보는 삭제한다.
                userRepository.delete(user);
                sessionCarrier.add("fail", true);
            } else {    // 이미 있는 회원이므로 토큰을 발급해주고 끝낸다.

                String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRoles());
                String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRoles());

                jwtTokenProvider.setHeaderAccessToken(response, accessToken);
                jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

                redisService.setValues(refreshToken, user.getEmail());

                sessionCarrier.add("nickname", user.getNickname()); // 클라이언트 요청 데이터1
                sessionCarrier.add("userRole", user.getUserRole()); // 클라이언트 요청 데이터2
            }
        } else {    // 신규 회원이면 가가입을 시킨다. 이후 클라이언트에선 바로 signUp으로 보내고 위 signUp Api에서 처리하게 된다.
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

    @Transactional  // 닉네임이 중복되는지 확인하는 기능이다.
    public boolean checkNickname(String nickname) {
        boolean nicknameDuplicate = userRepository.existsByNickname(nickname);
        return !nicknameDuplicate;
    }

    @Transactional  // 로컬 테스트 용 회원가입 코드
    public TokenResponse createToken(TestRequestDto testRequestDto, HttpServletResponse response) {
        // test code
        if (userRepository.existsByNickname(testRequestDto.getNickname())) {
            throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        User user = User.builder()
                .email(testRequestDto.getEmail())
                .introduction(testRequestDto.getIntroduction())
                .userRole(testRequestDto.getUserRole())
                .nickname(testRequestDto.getNickname())
                .createdDate(testRequestDto.getCreatedDate())
                .modifiedDate(testRequestDto.getModifiedDate())
                .grade(0.0)
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

    @Transactional  // 로컬 테스트 용 토큰 확인용 코드
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
