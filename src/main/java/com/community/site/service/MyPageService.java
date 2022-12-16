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
import org.springframework.data.domain.*;
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
        User user = returnUser(request, response);
        UserResponseDto userResponseDto = new UserResponseDto(user);

        return userResponseDto;
    }

    @Transactional
    public String averageGrade(HttpServletRequest request, HttpServletResponse response) {
        User user = returnUser(request, response);

        return String.format("%.1f", user.getGrade());
    }

    @Transactional
    public Page<ReviewResponseDto> viewReviewList(HttpServletRequest request, HttpServletResponse response,
                                                  int page) {
        User user = returnUser(request, response);

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<Review> reviews = reviewRepository.findByUser(user, pageable);

        return new PageImpl<>(reviews.stream().map(ReviewResponseDto::new).collect(Collectors.toList()),
                pageable, reviews.getSize());
    }

    @Transactional
    public Page<ThumbnailResponseDto> viewParticipatedBoardList(HttpServletRequest request, HttpServletResponse response,
                                           int page) {
        User user = returnUser(request, response);

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<BoardList> boardLists = boardRepository.findBySelectedArtist(user, pageable);

        return new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new).collect(Collectors.toList()),
                pageable, boardLists.getSize());
    }

    @Transactional
    public void updateMyPage(UserMyPageRequestDto userDto, HttpServletRequest request,
                             HttpServletResponse response) {    // 내 정보 업데이트
        User user = returnUser(request, response);
        user.updateMyPage(userDto);
    }

    @Transactional
    public void writeReviewAndGrade(ReviewRequestDto requestDto, HttpServletRequest request,
                                    HttpServletResponse response) {
        User user = returnUser(request, response);

        BoardList boardList = boardRepository.findById(requestDto.getRoomId()).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ErrorCode.ACCESS_DENIED_EXCEPTION); });

        List<Review> averageGrade = reviewRepository.findAllByUser(boardList.getSelectedArtist());
        Double averageSum = averageGrade.stream().mapToDouble(i -> i.getGrade()).sum() / averageGrade.size();

        reviewRepository.save(requestDto.builder()
                .roomId(requestDto.getRoomId())
                .content(requestDto.getContent())
                .grade(requestDto.getGrade())
                .nickname(user.getNickname())
                .user(boardList.getSelectedArtist())
                .boardList(boardList)
                .createdDate(requestDto.getCreatedDate())
                .build().toEntity());

        boardList.changeQuestEnum(COMPLETE);
        user.updateAverageGrade(averageSum);
    }

    @Transactional
    public void resign(HttpServletRequest request, HttpServletResponse response) {    // 회원 탈퇴
        User user = returnUser(request, response);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        userRepository.delete(user);
        redisService.delValues(refreshToken);
    }

    private User returnUser(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow();
        return user;
    }
}
