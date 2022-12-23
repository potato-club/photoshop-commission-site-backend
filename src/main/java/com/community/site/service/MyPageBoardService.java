package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.BoardDto.ThumbnailResponseDto;
import com.community.site.entity.BoardList;
import com.community.site.entity.User;
import com.community.site.enumcustom.BoardEnumCustom;
import com.community.site.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.community.site.enumcustom.BoardEnumCustom.*;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class MyPageBoardService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BoardRepository boardRepository;
    private final TokenService tokenService;

    @Transactional  // 마이페이지에서 내가 작성한 BEFORE 타입의 글들을 전부 보여준다.
    public Page<ThumbnailResponseDto> myPageAllBeforeBoardList(HttpServletRequest request, HttpServletResponse response,
                                                            int page) {
        User user = returnUser(request, response);

        return myPageBoardList(user, BEFORE, page);
    }

    @Transactional  // 마이페이지에서 내가 작성한 REQUESTING 타입의 글들을 전부 보여준다.
    public Page<ThumbnailResponseDto> myPageAllRequestingBoardList(HttpServletRequest request, HttpServletResponse response,
                                                                int page) {
        User user = returnUser(request, response);

        return myPageBoardList(user, REQUESTING, page);
    }

    @Transactional  // 마이페이지에서 내가 작성한 COMPLETE 타입의 글들을 전부 보여준다.
    public Page<ThumbnailResponseDto> myPageAllCompleteBoardList(HttpServletRequest request, HttpServletResponse response,
                                                              int page) {
        User user = returnUser(request, response);

        return myPageBoardList(user, COMPLETE, page);
    }

    @Transactional  // 마이페이지에서 내가 작성한 BEFORE 타입의 글 8개를 미리보기로 보여준다.
    public List<ThumbnailResponseDto> myPageBeforeBoardList(HttpServletRequest request, HttpServletResponse response) {

        User user = returnUser(request, response);

        return myListBoardList(user, BEFORE);
    }

    @Transactional  // 마이페이지에서 내가 작성한 REQUESTING 타입의 글 8개를 미리보기로 보여준다.
    public List<ThumbnailResponseDto> myPageRequestingBoardList(HttpServletRequest request, HttpServletResponse response) {

        User user = returnUser(request, response);

        return myListBoardList(user, REQUESTING);
    }

    @Transactional  // 마이페이지에서 내가 작성한 COMPLETE 타입의 글 8개를 미리보기로 보여준다.
    public List<ThumbnailResponseDto> myPageCompleteBoardList(HttpServletRequest request, HttpServletResponse response) {

        User user = returnUser(request, response);

        return myListBoardList(user, COMPLETE);
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

    /*
        중복되는 코드들이 많아 따로 빼내어 정리한 코드이다.
        게시글 목록을 게시글 상태와 자신의 작성 유무에 따라 보여주는 코드이다.
     */
    private Page<ThumbnailResponseDto> myPageBoardList(User user, BoardEnumCustom boardEnumCustom, int page) {

        long totalElements = boardRepository.countByUserAndQuestEnum(user, boardEnumCustom);
        Pageable pageable = PageRequest.of(page - 1, 16);

        List<BoardList> boardLists = boardRepository.findByUserAndQuestEnumOrderByIdDesc(user, boardEnumCustom);

        Page<ThumbnailResponseDto> pageList = new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new)
                .collect(Collectors.toList()), pageable, totalElements);

        return pageList;
    }

    private List<ThumbnailResponseDto> myListBoardList(User user, BoardEnumCustom boardEnumCustom) {
        List<BoardList> boardLists = boardRepository.findByUserAndQuestEnumOrderByIdDesc(user, boardEnumCustom);

        return boardLists.stream().map(ThumbnailResponseDto::new).limit(8).collect(Collectors.toList());
    }
}
