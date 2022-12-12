package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.UserDto.UserNicknameDto;
import com.community.site.entity.BoardList;
import com.community.site.entity.User;
import com.community.site.enumcustom.BoardEnumCustom;
import com.community.site.enumcustom.UserRole;
import com.community.site.error.exception.UnAuthorizedException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.community.site.enumcustom.UserRole.ARTIST;
import static com.community.site.error.ErrorCode.ACCESS_DENIED_EXCEPTION;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardQuestService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private BoardList getBoardList(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
        {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        });
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
        {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        });
    }

    private Page<UserNicknameDto> pagingList(int page, List<UserNicknameDto> requestList) {
        Pageable pageable = PageRequest.of(page - 1, 5);

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), requestList.size());

        return new PageImpl<>(requestList.subList(start, end), pageable, requestList.size());
    }

    @Transactional
    public Page<UserNicknameDto> getRequestUserList(Long id, int page, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        BoardList boardList = getBoardList(id);
        List<UserNicknameDto> requestList;
        User user = getUserByEmail(email);

        if (!boardList.getUser().equals(user)) {
            throw new UnAuthorizedException("게시글 작성자만 가능합니다.", ACCESS_DENIED_EXCEPTION);
        }

        requestList = boardList.getRequestList().stream()
                .map(n -> UserNicknameDto.builder().nickname(n).build()).collect(Collectors.toList());

        return pagingList(page, requestList);
    }

    @Transactional
    public void acceptQuest(Long id, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        BoardList boardList = getBoardList(id);
        User user = getUserByEmail(email);

        if (!user.getUserRole().equals(ARTIST)) {
            throw new UnAuthorizedException("ARTIST 유저만 가능합니다", ACCESS_DENIED_EXCEPTION);
        } else if(boardList.getRequestList().contains(user.getNickname())) {
            throw new UnAuthorizedException("이미 요청된 상태입니다.", ACCESS_DENIED_EXCEPTION);
        }

        boardList.updateAcceptQuest(user.getNickname());
    }

    @Transactional
    public void chooseArtist(Long id, UserNicknameDto artistDto, HttpServletRequest request) {
        User artist = userRepository.findByNickname(artistDto.getNickname());
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        BoardList boardList = getBoardList(id);
        User user = getUserByEmail(email);

        if (!boardList.getUser().equals(user)) {
            throw new UnAuthorizedException("게시글 작성자만 가능합니다.", ACCESS_DENIED_EXCEPTION);
        }

        boardList.choiceArtist(artist, BoardEnumCustom.REQUESTING);
    }
}
