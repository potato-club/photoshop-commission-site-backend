package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.BoardDto.ThumbnailResponseDto;
import com.community.site.entity.BoardList;
import com.community.site.entity.User;
import com.community.site.jwt.JwtTokenProvider;
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

    @Transactional
    public Page<ThumbnailResponseDto> myPageAllBeforeBoardList(HttpServletRequest request, HttpServletResponse response,
                                                            int page) {
        User user = returnUser(request, response);

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<BoardList> boardLists = boardRepository.findAllByUser(user, pageable);

        return new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new)
                .filter(i -> i.getQuestEnum().equals(BEFORE)).collect(Collectors.toList()),
                pageable, boardLists.getTotalPages());
    }

    @Transactional
    public Page<ThumbnailResponseDto> myPageAllRequestingBoardList(HttpServletRequest request, HttpServletResponse response,
                                                                int page) {
        User user = returnUser(request, response);

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<BoardList> boardLists = boardRepository.findAllByUser(user, pageable);

        return new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new)
                .filter(i -> i.getQuestEnum().equals(REQUESTING)).collect(Collectors.toList()),
                pageable, boardLists.getTotalPages());
    }

    @Transactional
    public Page<ThumbnailResponseDto> myPageAllCompleteBoardList(HttpServletRequest request, HttpServletResponse response,
                                                              int page) {
        User user = returnUser(request, response);

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<BoardList> boardLists = boardRepository.findAllByUser(user, pageable);

        return new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new)
                .filter(i -> i.getQuestEnum().equals(COMPLETE)).collect(Collectors.toList()),
                pageable, boardLists.getTotalPages());
    }

    @Transactional
    public List<ThumbnailResponseDto> myPageBeforeBoardList(HttpServletRequest request, HttpServletResponse response) {

        User user = returnUser(request, response);
        List<BoardList> boardLists = boardRepository.findByUser(user);

        return boardLists.stream().map(ThumbnailResponseDto::new).limit(8).collect(Collectors.toList());
    }

    @Transactional
    public List<ThumbnailResponseDto> myPageRequestingBoardList(HttpServletRequest request, HttpServletResponse response) {

        User user = returnUser(request, response);
        List<BoardList> boardLists = boardRepository.findByUser(user);

        return boardLists.stream().map(ThumbnailResponseDto::new).limit(8).collect(Collectors.toList());
    }

    @Transactional
    public List<ThumbnailResponseDto> myPageCompleteBoardList(HttpServletRequest request, HttpServletResponse response) {

        User user = returnUser(request, response);
        List<BoardList> boardLists = boardRepository.findByUser(user);

        return boardLists.stream().map(ThumbnailResponseDto::new).limit(8).collect(Collectors.toList());
    }

    private User returnUser(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow();
        return user;
    }
}
