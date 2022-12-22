package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.CommentRepository;
import com.community.site.Repository.ReviewRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.BoardDto.ThumbnailResponseDto;
import com.community.site.dto.BoardDto.UserReviewResponseDto;
import com.community.site.dto.CommentDto.MyCommentResponseDto;
import com.community.site.dto.ReviewDto.ReviewRequestDto;
import com.community.site.dto.ReviewDto.ReviewResponseDto;
import com.community.site.dto.UserDto.UserMyPageRequestDto;
import com.community.site.dto.UserDto.UserResponseDto;
import com.community.site.entity.BoardList;
import com.community.site.entity.Comment;
import com.community.site.entity.Review;
import com.community.site.entity.User;
import com.community.site.error.ErrorCode;
import com.community.site.error.exception.UnAuthorizedException;
import com.community.site.jwt.JwtTokenProvider;
import com.community.site.service.Jwt.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.community.site.enumcustom.BoardEnumCustom.COMPLETE;
import static com.community.site.enumcustom.BoardEnumCustom.REQUESTING;

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
    private final CommentRepository commentRepository;

    @Transactional  // 마이페이지에서 자신의 정보를 불러온다.
    public UserResponseDto viewMyPage(HttpServletRequest request, HttpServletResponse response) {     // 내 정보 보기
        User user = returnUser(request, response);
        UserResponseDto userResponseDto = new UserResponseDto(user);

        return userResponseDto;
    }

    @Transactional  // User 엔티티에 저장된 평균 평점을 불러온다.
    public String averageGrade(HttpServletRequest request, HttpServletResponse response) {
        User user = returnUser(request, response);

        return String.format("%.1f", user.getGrade());
    }

    @Transactional  // 자기가 작성한 글 중 의뢰가 성립된 글을 보여준다.
    public Page<UserReviewResponseDto> viewReviewListToMe(HttpServletRequest request, HttpServletResponse response,
                                                            int page) {
        User user = returnUser(request, response);

        long totalElements = boardRepository.countByUser(user);
        Pageable pageable = PageRequest.of(page - 1, 6);

        List<BoardList> boardLists = boardRepository.findByUser(user);
        Collections.reverse(boardLists);

        Page<UserReviewResponseDto> pageList = new PageImpl<>(boardLists.stream().filter(i -> i.getQuestEnum()
                        .equals(REQUESTING)).map(UserReviewResponseDto::new)
                        .collect(Collectors.toList()), pageable, totalElements);

        return pageList;
    }

    @Transactional  // 작성한 댓글들을 리스트로 볼 수 있다.
    public Page<MyCommentResponseDto> viewComments(HttpServletRequest request, HttpServletResponse response, int page) {
        User user = returnUser(request, response);

        long totalElements = commentRepository.countByUser(user);
        Pageable pageable = PageRequest.of(page - 1, 6);

        List<Comment> comments = commentRepository.findByUser(user);
        Collections.reverse(comments);

        Page<MyCommentResponseDto> pageList = new PageImpl<>(comments.stream().map(MyCommentResponseDto::new)
                .collect(Collectors.toList()), pageable, totalElements);

        return pageList;
    }

    @Transactional  // 작성된 후기들을 볼 수 있다. (ARTIST 관점)
    public Page<ReviewResponseDto> viewReviewList(HttpServletRequest request, HttpServletResponse response,
                                                  int page) {
        User user = returnUser(request, response);

        long totalElements = reviewRepository.countByUser(user);
        Pageable pageable = PageRequest.of(page - 1, 16);

        List<Review> reviews = reviewRepository.findByUser(user);
        Collections.reverse(reviews);

        Page<ReviewResponseDto> pageList = new PageImpl<>(reviews.stream().map(ReviewResponseDto::new)
                .collect(Collectors.toList()), pageable, totalElements);

        return pageList;
    }

    @Transactional  // 자신의 Enum이 ARTIST일 때 의뢰를 수주한 게시글들을 출력해준다.
    public Page<ThumbnailResponseDto> viewParticipatedBoardList(HttpServletRequest request, HttpServletResponse response,
                                           int page) {
        User user = returnUser(request, response);

        long totalElements = boardRepository.countBySelectedArtist(user);
        Pageable pageable = PageRequest.of(page - 1, 16);

        List<BoardList> boardLists = boardRepository.findBySelectedArtist(user);
        Collections.reverse(boardLists);

        Page<ThumbnailResponseDto> pageList = new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new)
                .collect(Collectors.toList()), pageable, totalElements);

        return pageList;
    }

    // 내 정보 업데이트
    @Transactional  // 마이페이지 정보를 수정한다.
    public void updateMyPage(UserMyPageRequestDto userDto, HttpServletRequest request,
                             HttpServletResponse response) throws IllegalAccessException {

        User user = returnUser(request, response);

        /*
         클라이언트로부터 받은 UserMyPageRequestDto의 컬럼 값들이 null인지 아닌지를 구분하여
         null일 시 기존 User 엔티티의 해당 컬럼 값을 넣어 한번에 update 시키고자 사용했다.
         */
        for (Field field : UserMyPageRequestDto.class.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(userDto);
            if (value == null) {
                try {
                    Field userField = User.class.getDeclaredField(field.getName());
                    userField.setAccessible(true);
                    Object userValue = userField.get(user);
                    field.set(userDto, userValue);
                } catch (IllegalAccessException e) {
                    log.error("Error setting field value: {}", e.getMessage());
                } catch (NoSuchFieldException e) {
                    log.error("Error getting field: {}", e.getMessage());
                }
            }
        }

        user.updateMyPage(userDto);
    }

    @Transactional  // 리뷰와 평점을 작성하고 게시글 Enum을 COMPLETE로 바꾼다.
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

    @Transactional  // 회원 탈퇴 기능
    public void resign(HttpServletRequest request, HttpServletResponse response) {
        User user = returnUser(request, response);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        userRepository.delete(user);
        redisService.delValues(refreshToken);
    }

    /*
        중복되는 코드들이 많아 따로 빼내어 정리한 코드이다.
        토큰 값을 검증하고 accessToken이 만료됐을 시 자동으로 재발급까지 해준다.
        확인이 양호하다면 토큰에서 email 값을 추출하여 User 정보를 찾아온다.
        User 정보를 성공적으로 찾았으면 반환하고 끝난다.
     */
    private User returnUser(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow();
        return user;
    }
}
