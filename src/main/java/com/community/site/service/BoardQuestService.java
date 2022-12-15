package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.UserDto.UserNicknameDto;
import com.community.site.entity.BoardList;
import com.community.site.entity.User;
import com.community.site.enumcustom.BoardEnumCustom;
import com.community.site.error.exception.DuplicateException;
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
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.community.site.enumcustom.UserRole.ARTIST;
import static com.community.site.error.ErrorCode.ACCESS_DENIED_EXCEPTION;
import static com.community.site.error.ErrorCode.CONFLICT_EXCEPTION;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardQuestService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    private BoardList getBoardList(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
        {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        });
    }

    @Transactional
    public User getUserByToken(HttpServletRequest request,
                                HttpServletResponse response) {
        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);
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
    public Page<UserNicknameDto> getRequestUserList(Long id, int page, HttpServletRequest request,
                                                    HttpServletResponse response) {
        BoardList boardList = getBoardList(id);
        List<UserNicknameDto> requestList;
        User user = getUserByToken(request, response);

        if (!boardList.getUser().equals(user)) {
            throw new UnAuthorizedException("게시글 작성자만 가능합니다.", ACCESS_DENIED_EXCEPTION);
        }

        requestList = boardList.getRequestList().stream()
                .map(n -> UserNicknameDto.builder().nickname(n).build()).collect(Collectors.toList());

        return pagingList(page, requestList);
    }

    @Transactional
    public void acceptQuest(Long id, HttpServletRequest request,
                            HttpServletResponse response) {
        BoardList boardList = getBoardList(id);
        User user = getUserByToken(request, response);

        if (!user.getUserRole().equals(ARTIST)) {
            throw new UnAuthorizedException("ARTIST 유저만 가능합니다", ACCESS_DENIED_EXCEPTION);
        } else if(boardList.getRequestList().contains(user.getNickname())) {
            throw new DuplicateException("이미 요청된 상태입니다.", CONFLICT_EXCEPTION);
        }

        boardList.addAcceptQuest(user.getNickname());
    }

    @Transactional
    public void exceptQuest(Long id, HttpServletRequest request,
                            HttpServletResponse response) {
        BoardList boardList = getBoardList(id);
        User user = getUserByToken(request, response);

        if (!user.getUserRole().equals(ARTIST)) {
            throw new UnAuthorizedException("ARTIST 유저만 가능합니다", ACCESS_DENIED_EXCEPTION);
        } else if(!boardList.getRequestList().contains(user.getNickname())) {
            throw new UnAuthorizedException("요청하지 않은 상태입니다.", ACCESS_DENIED_EXCEPTION);
        }

        boardList.removeAcceptQuest(user.getNickname());
    }

    @Transactional
    public void chooseArtist(Long id, UserNicknameDto artistDto, HttpServletRequest request,
                             HttpServletResponse response) {
        User artist = userRepository.findByNickname(artistDto.getNickname());
        BoardList boardList = getBoardList(id);
        User user = getUserByToken(request, response);

        if (!boardList.getUser().equals(user)) {
            throw new UnAuthorizedException("게시글 작성자만 가능합니다.", ACCESS_DENIED_EXCEPTION);
        }

        boardList.choiceArtist(artist, BoardEnumCustom.REQUESTING);
    }
}
