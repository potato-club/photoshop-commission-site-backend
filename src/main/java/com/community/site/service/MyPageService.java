package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.ReviewRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.BoardDto.ThumbnailResponseDto;
import com.community.site.dto.ReviewDto.ReviewRequestDto;
import com.community.site.dto.ReviewDto.ReviewResponseDto;
import com.community.site.dto.UserDto.UserMyPageRequestDto;
import com.community.site.dto.UserDto.UserRequestDto;
import com.community.site.dto.UserDto.UserResponseDto;
import com.community.site.entity.BoardList;
import com.community.site.entity.Review;
import com.community.site.entity.User;
import com.community.site.error.ErrorCode;
import com.community.site.error.exception.UnAuthorizedException;
import com.community.site.jwt.JwtTokenProvider;
import com.community.site.service.Jwt.RedisService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static com.community.site.enumcustom.BoardEnumCustom.COMPLETE;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class MyPageService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BoardRepository boardRepository;
    private final ReviewRepository reviewRepository;
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
    public String averageGrade(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION); });

        return String.format("%.1f", user.getGrade());
    }

    @Transactional
    public Page<ReviewResponseDto> viewReviewList(HttpServletRequest request, HttpServletResponse response,
                                                  int page) {
        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow();

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<Review> reviews = reviewRepository.findByUser(user, pageable);

        return new PageImpl<>(reviews.stream().map(ReviewResponseDto::new).collect(Collectors.toList()),
                pageable, reviews.getTotalPages());
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
    public void writeReviewAndGrade(ReviewRequestDto requestDto, HttpServletRequest request,
                                    HttpServletResponse response) {
        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION); });

        BoardList boardList = boardRepository.findById(requestDto.getRoomId()).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION); });

        requestDto.builder()
                .nickname(user.getNickname())
                .user(boardList.getSelectedArtist())
                .boardList(boardList)
                .build();

        List<Review> averageGrade = reviewRepository.findAllByUser(boardList.getSelectedArtist());
        Double averageSum = averageGrade.stream().mapToDouble(i -> i.getGrade()).sum() / averageGrade.size();

        reviewRepository.save(requestDto.toEntity());
        boardList.changeQuestEnum(COMPLETE);
        user.updateAverageGrade(averageSum);
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
