package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.UserDto.UserNicknameDto;
import com.community.site.entity.BoardList;
import com.community.site.entity.User;
import com.community.site.enumcustom.BoardEnumCustom;
import com.community.site.error.exception.UnAuthorizedException;
import com.community.site.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public List<UserNicknameDto> getRequestUserList(Long id, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        BoardList boardList = boardRepository.findById(id).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        List<UserNicknameDto> requestList = new ArrayList<>();

        if (!boardList.getUser().getEmail().equals(email)) {
            throw new UnAuthorizedException("게시글 작성자만 확인 가능합니다.", ACCESS_DENIED_EXCEPTION);
        }

        for (String nickname : boardList.getRequestList()) {
            requestList.add(UserNicknameDto.builder().nickname(nickname).build());
        }

        return requestList;
    }

    @Transactional
    public void acceptQuest(Long id, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        BoardList boardList = boardRepository.findById(id).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        if (!user.getUserRole().equals(ARTIST)) {
            throw new UnAuthorizedException("ARTIST 유저만 가능합니다", ACCESS_DENIED_EXCEPTION);
        } else if (boardList.getRequestList().equals(user)) {

        }

        boardList.updateAcceptQuest(user.getNickname());
    }

    @Transactional
    public void chooseArtist(Long id, UserNicknameDto artistDto) {
        BoardList boardList = boardRepository.findById(id).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        User artist = userRepository.findByNickname(artistDto.getNickname());

        boardList.choiceArtist(artist, BoardEnumCustom.REQUESTING);
    }
}
