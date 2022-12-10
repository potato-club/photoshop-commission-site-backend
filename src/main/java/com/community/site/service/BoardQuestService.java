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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<BoardList> boardList = boardRepository.findById(id);
        List<UserNicknameDto> requestList = new ArrayList<>();

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        if (boardList.isEmpty()) {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        } else if(!boardList.get().getUser().equals(user)) {
            throw new UnAuthorizedException("게시글 작성자만 확인 가능합니다.", ACCESS_DENIED_EXCEPTION);
        }

        for (String nickname : boardList.get().getRequestList()) {
            requestList.add(UserNicknameDto.builder().nickname(nickname).build());
        }

        return requestList;
    }

    @Transactional
    public void acceptQuest(Long id, HttpServletRequest request) {
        String email = jwtTokenProvider.getUserEmail(jwtTokenProvider.resolveAccessToken(request));
        Optional<BoardList> boardList = boardRepository.findById(id);
        System.out.println(email);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        System.out.println(user.getUserRole());
        if (user.getUserRole() != UserRole.ARTIST) {
            throw new UnAuthorizedException("ARTIST 유저만 가능합니다", ACCESS_DENIED_EXCEPTION);
        } else if (boardList.isEmpty()) {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        }

        boardList.get().updateAcceptQuest(user.getNickname());
    }

    @Transactional
    public void chooseArtist(Long id, UserNicknameDto artistDto) {
        Optional<BoardList> boardList = boardRepository.findById(id);
        User artist = userRepository.findByNickname(artistDto.getNickname());

        if (boardList.isEmpty()) {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        }

        boardList.get().choiceArtist(artist, BoardEnumCustom.REQUESTING);
    }
}
