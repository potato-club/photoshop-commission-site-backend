package com.community.site.service;

import com.community.site.dto.BoardDto.*;
import com.community.site.entity.File;
import com.community.site.enumcustom.ImageOpen;
import com.community.site.service.S3.S3UploadService;
import com.community.site.Repository.BoardRepository;
import com.community.site.Repository.FileRepository.FileRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.entity.BoardList;
import com.community.site.entity.User;
import com.community.site.error.exception.UnAuthorizedException;
import com.community.site.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.community.site.enumcustom.BoardEnumCustom.*;
import static com.community.site.error.ErrorCode.ACCESS_DENIED_EXCEPTION;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final FileRepository fileRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private String nickname;

    @Transactional  // 검색 기능 중 타이틀로 검색하기 기능이다. 한 페이지 당 16개씩 출력한다.
    public Page<ThumbnailResponseDto> getTitleBoardList(String keyword, int page) {

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<BoardList> boardLists = boardRepository.findByTitle(keyword, pageable);

        return new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new).collect(Collectors.toList()),
                pageable, boardLists.getSize());
    }

    @Transactional  // 검색 기능 중 닉네임으로 검색하기 기능이다. 한 페이지 당 16개씩 출력한다.
    public Page<ThumbnailResponseDto> getNicknameBoardList(String keyword, int page) {

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<BoardList> boardLists = boardRepository.findByNickname(keyword, pageable);

        return new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new).collect(Collectors.toList()),
                pageable, boardLists.getSize());
    }

    @Transactional  // 게시글 상세보기 기능이다.
    public BoardResponseDto findBoardList(Long id, HttpServletRequest request,
                                          HttpServletResponse response) {

        String token = tokenService.validateAndReissueToken(request, response);

        if (token.equals("guest")) {
            nickname = "GUEST";
        } else {
            String email = jwtTokenProvider.getUserEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow();

            nickname = user.getNickname();
        }

        BoardList boardLists = boardRepository.findById(id).orElseThrow();
        BoardResponseDto boardResponseDto = new BoardResponseDto(boardLists, nickname);

        return boardResponseDto;
    }

    @Transactional  // BEFORE 타입의 글들을 16개씩 보여준다. (Pagenation 적용)
    public Page<ThumbnailResponseDto> getAllBeforeBoardList(int page) {

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<BoardList> boardLists = boardRepository.findAllByQuestEnum(BEFORE, pageable);

        return new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new).collect(Collectors.toList()),
                pageable, boardLists.getSize());
    }

    @Transactional  // REQUESTING 타입의 글들을 16개씩 보여준다. (Pagenation 적용)
    public Page<ThumbnailResponseDto> getAllRequestingBoardList(int page) {

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<BoardList> boardLists = boardRepository.findAllByQuestEnum(REQUESTING, pageable);

        return new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new).collect(Collectors.toList()),
                pageable, boardLists.getSize());
    }

    @Transactional  // COMPLETE 타입의 글들을 16개씩 보여준다. (Pagenation 적용)
    public Page<ThumbnailResponseDto> getAllCompleteBoardList(int page) {

        Pageable pageable = PageRequest.of(page - 1, 16);
        Page<BoardList> boardLists = boardRepository.findAllByQuestEnum(COMPLETE, pageable);

        return new PageImpl<>(boardLists.stream().map(ThumbnailResponseDto::new).collect(Collectors.toList()),
                pageable, boardLists.getSize());
    }

    @Transactional  // BEFORE 타입의 최신 글 8개를 메인페이지에서 보여주는 기능이다.
    public List<ThumbnailResponseDto> getBeforeBoardList() {

        List<BoardList> boardLists = boardRepository.findByQuestEnum(BEFORE);
        boardLists.sort(Comparator.comparingLong(BoardList::getId));

        return boardLists.stream().map(ThumbnailResponseDto::new).limit(8).collect(Collectors.toList());
    }

    @Transactional  // REQUESTING 타입의 최신 글 8개를 메인페이지에서 보여주는 기능이다.
    public List<ThumbnailResponseDto> getRequestingBoardList() {

        List<BoardList> boardLists = boardRepository.findByQuestEnum(REQUESTING);

        return boardLists.stream().map(ThumbnailResponseDto::new).limit(8).collect(Collectors.toList());
    }

    @Transactional  // COMPLETE 타입의 최신 글 8개를 메인페이지에서 보여주는 기능이다.
    public List<ThumbnailResponseDto> getCompleteBoardList() {

        List<BoardList> boardLists = boardRepository.findByQuestEnum(COMPLETE);

        return boardLists.stream().map(ThumbnailResponseDto::new).limit(8).collect(Collectors.toList());
    }

    @Transactional  // 게시글 작성 기능이다.
    public void createBoard(List<MultipartFile> image, ImageOpen imageOpen,
                                          BoardRequestDto boardListDto,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {

        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        boardListDto.setUser(user);
        boardListDto.setImageOpen(imageOpen);
        boardListDto.setQuestEnum(BEFORE);

        BoardList boardList = boardListDto.toEntity();
        boardRepository.save(boardList);

        uploadBoardListFile(image, boardList);
    }

    // 사진을 S3 저장소에 올리고 그 요소들을 리스트로 반환하는 기능이다. (리스트 반환은 게시글 수정 2차 개발에서 사용할 예정이다.)
    private List<String> uploadBoardListFile(List<MultipartFile> image, BoardList boardList) {
        return image.stream()
                .map(file -> s3UploadService.uploadFile(file))
                .map(url -> createFile(boardList, url))
                .map(file -> file.getFileUrl())
                .collect(Collectors.toList());
    }

    // DB에 올린 사진의 Url과 이름을 저장하는 기능이다.
    private File createFile(BoardList boardList, String url) {
        return fileRepository.save(File.builder()
                .fileUrl(url)
                .fileName(StringUtils.getFilename(url))
                .boardList(boardList)
                .build());
    }

    @Transactional  // 이미지 수정 2차 개발로 연기(return 타입 UploadFileResponse 추후 사용)
    public void updateBoard(Long id, BoardUpdateRequestDto boardListDto, ImageOpen imageOpen,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {

        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        BoardList boardList = boardRepository.findById(id).orElseThrow(() ->
            { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        if (!boardList.getUser().getNickname().equals(user.getNickname())) {
            throw new UnAuthorizedException("NOT_FOUND_POST", ACCESS_DENIED_EXCEPTION);
        }

        boardListDto.setImageOpen(imageOpen);
        boardList.update(boardListDto);

        //  2차 개발
//        validateDeletedFiles(boardListDto);
//        uploadFiles(boardListDto, boardList);
//
//
//        List<String> downloadUri = new ArrayList<>();
//
//        for (MultipartFile Link : boardListDto.getImage()) {
//            downloadUri.add(Link.getOriginalFilename());
//        }
//
//        UploadFileResponse uploadFileResponse = new UploadFileResponse(boardListDto.getId(), downloadUri);
    }

    @Transactional  // 게시글 삭제 기능이다.
    public void deleteBoard(Long id, HttpServletRequest request, HttpServletResponse response) {

        String token = tokenService.validateAndReissueToken(request, response);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        BoardList boardList = boardRepository.findById(id).orElseThrow(() ->
            { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        if (!boardList.getUser().getNickname().equals(user.getNickname())) {
            throw new UnAuthorizedException("NOT_FOUND_POST", ACCESS_DENIED_EXCEPTION);
        }

        boardRepository.delete(boardList);
    }

    // 2차 개발
    //    private void validateDeletedFiles(BoardUpdateRequestDto boardListDto) {
//        fileRepository.findBySavedFileUrl(boardListDto.getId()).stream()
//                .filter(file -> !boardListDto.getSavedFileUrl().stream().anyMatch(Predicate.isEqual(file.getFileUrl())))
//                .forEach(url -> {
//                    fileRepository.delete(url);
//                    s3UploadService.deleteFile(url.getFileUrl());
//                });
//    }
//
//    private void uploadFiles(BoardUpdateRequestDto boardListDto, BoardList boardList) {
//        boardListDto.getImage()
//                .stream()
//                .forEach(file -> {
//                    String url = s3UploadService.uploadFile(file);
//                    createFile(boardList, url);
//                });
//    }
}
