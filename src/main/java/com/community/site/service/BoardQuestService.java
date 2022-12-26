package com.community.site.service;

import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.OutputRepository.OutputRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.BoardDto.BoardOutputResponseDto;
import com.community.site.dto.UserDto.UserNicknameDto;
import com.community.site.entity.BoardList;
import com.community.site.entity.Output;
import com.community.site.entity.User;
import com.community.site.enumcustom.BoardEnumCustom;
import com.community.site.error.exception.DuplicateException;
import com.community.site.error.exception.UnAuthorizedException;
import com.community.site.jwt.JwtTokenProvider;
import com.community.site.service.S3.S3UploadService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
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
    private final OutputRepository outputRepository;
    private final S3UploadService s3UploadService;
    private String nickname;

    private BoardList getBoardList(Long id) {   // 공통된 부분이 많아 따로 빼놓은 기능이다. BoardList를 반환한다.
        return boardRepository.findById(id).orElseThrow(() ->
        {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        });
    }

    // 토큰 검증 및 accessToken 만료 시 재발급해주며 이 토큰값으로 email을 추출하여 User 값을 반환하는 기능이다.
    private User getUserByToken(HttpServletRequest request,
                                HttpServletResponse response) {
        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);
        return userRepository.findByEmail(email).orElseThrow(() ->
        {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        });
    }

    // Pagenation 처리 로직을 공통화하기 위해 만든 기능이다.
    private Page<UserNicknameDto> pagingList(int page, List<UserNicknameDto> requestList) {
        Pageable pageable = PageRequest.of(page - 1, 5);

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), requestList.size());

        return new PageImpl<>(requestList.subList(start, end), pageable, requestList.size());
    }

    @Transactional  // 신청한 ARTIST 목록을 띄워주는 기능이다.
    public Page<UserNicknameDto> getRequestUserList(Long id, int page, HttpServletRequest request,
                                                    HttpServletResponse response) {
        BoardList boardList = getBoardList(id);
        List<UserNicknameDto> requestList = new ArrayList<>();
        User user = getUserByToken(request, response);

        if (!boardList.getUser().equals(user)) {
            throw new UnAuthorizedException("게시글 작성자만 가능합니다.", ACCESS_DENIED_EXCEPTION);
        }

        for (String nickname : boardList.getRequestList()) {
            Double grade = userRepository.findByNickname(nickname).getGrade();
            requestList.add(UserNicknameDto.builder()
                            .nickname(nickname)
                            .grade(Double.valueOf(String.format("%.1f", grade)))
                            .build());
        }

        return pagingList(page, requestList);
    }

    @Transactional  // ARTIST가 의뢰 수락을 하기 위한 기능이다.
    public void acceptQuest(Long id, HttpServletRequest request,
                            HttpServletResponse response) {
        BoardList boardList = getBoardList(id);
        User user = getUserByToken(request, response);

        if (!user.getUserRole().equals(ARTIST)) {
            throw new UnAuthorizedException("ARTIST 유저만 가능합니다", ACCESS_DENIED_EXCEPTION);
        } else if (boardList.getRequestList().contains(user.getNickname())) {
            throw new DuplicateException("이미 요청된 상태입니다.", CONFLICT_EXCEPTION);
        } else if (user.getNickname().equals(boardList.getNickname())) {
            throw new UnAuthorizedException("본인 글에 본인이 신청하는 것은 불가능합니다.", ACCESS_DENIED_EXCEPTION);
        }

        boardList.addAcceptQuest(user.getNickname());
    }

    @Transactional  // ARTIST 유저가 신청한 의뢰를 취소하기 위한 기능이다.
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

    @Transactional  // 의뢰자가 ARTIST 리스트 중에서 한 사람을 정할 때 쓰는 기능이다.
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

    @Transactional  // 작업 결과물을 저장할 때 쓰는 기능이다.
    public void uploadOutput(Long id, List<MultipartFile> image,
                             HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByToken(request, response);
        BoardList boardList = getBoardList(id);

        if (user.getNickname().equals(boardList.getSelectedArtist().getNickname())) {
            uploadBoardListOutput(image, boardList);
        } else {
            throw new JwtException("연결된 디자이너가 아닙니다.");
        }
    }

    @Transactional  // 올린 작업 결과물을 보여주는 기능이다.
    public BoardOutputResponseDto viewOutputs(Long id, HttpServletRequest request,
                                         HttpServletResponse response) {
        BoardList boardList = getBoardList(id);
        String accessToken = tokenService.validateAndReissueToken(request, response);

        if (accessToken.equals("guest")) {
            nickname = "GUEST";
        } else {
            String email = jwtTokenProvider.getUserEmail(accessToken);
            User user = userRepository.findByEmail(email).orElseThrow();

            nickname = user.getNickname();
        }

        BoardOutputResponseDto responseDto = new BoardOutputResponseDto(boardList, nickname);
        return responseDto;
    }

    private List<String> uploadBoardListOutput(List<MultipartFile> image, BoardList boardList) {
        return image.stream()
                .map(file -> s3UploadService.uploadFile(file))
                .map(url -> createFile(boardList, url))
                .map(file -> file.getFileUrl())
                .collect(Collectors.toList());
    }

    private Output createFile(BoardList boardList, String url) {
        return outputRepository.save(Output.builder()
                .fileUrl(url)
                .fileName(StringUtils.getFilename(url))
                .boardList(boardList)
                .build());
    }
}
