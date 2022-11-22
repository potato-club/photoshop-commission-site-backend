package com.community.site.service;

import com.community.site.Repository.BoardRepository.BoardRepository;
import com.community.site.Repository.FileRepository.FileRepository;
import com.community.site.Repository.UserRepository;
import com.community.site.dto.BoardDto.*;
import com.community.site.entity.BoardList;
import com.community.site.entity.File;
import com.community.site.entity.User;
import com.community.site.error.exception.UnAuthorizedException;
import com.community.site.jwt.JwtTokenProvider;
import com.community.site.service.S3.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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

    @Transactional
    public List<ThumbnailResponseDto> getTitleBoardList(String keyword) {
        Optional<BoardList> boardLists = boardRepository.findByTitle(keyword);
        return boardLists.stream().map(ThumbnailResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public List<ThumbnailResponseDto> getNicknameBoardList(String keyword) {
        Optional<BoardList> boardLists = boardRepository.findByNickname(keyword);
        return boardLists.stream().map(ThumbnailResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public List<BoardResponseDto> findBoardList(Long id) {
        if (boardRepository.getById(id).equals("")) {
            throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION);
        }

        Optional<BoardList> boardLists = boardRepository.findById(id);
        return boardLists.stream().map(BoardResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public List<ThumbnailResponseDto> getAllBeforeBoardList() {

        List<BoardList> boardLists = boardRepository.findAll();

        return boardLists.stream().map(ThumbnailResponseDto::new).filter(b -> b.getQuestEnum().equals(BEFORE))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ThumbnailResponseDto> getAllRequestingBoardList() {

        List<BoardList> boardLists = boardRepository.findAll();

        return boardLists.stream().map(ThumbnailResponseDto::new).filter(b -> b.getQuestEnum().equals(REQUESTING))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ThumbnailResponseDto> getAllCompleteBoardList() {

        List<BoardList> boardLists = boardRepository.findAll();

        return boardLists.stream().map(ThumbnailResponseDto::new).filter(b -> b.getQuestEnum().equals(COMPLETE))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ThumbnailResponseDto> getBeforeBoardList() {

        List<BoardList> boardLists = boardRepository.findAll();

        return boardLists.stream().map(ThumbnailResponseDto::new).filter(b -> b.getQuestEnum().equals(BEFORE))
                .limit(8).collect(Collectors.toList());
    }

    @Transactional
    public List<ThumbnailResponseDto> getRequestingBoardList() {

        List<BoardList> boardLists = boardRepository.findAll();

        return boardLists.stream().map(ThumbnailResponseDto::new).filter(b -> b.getQuestEnum().equals(REQUESTING))
                .limit(8).collect(Collectors.toList());
    }

    @Transactional
    public List<ThumbnailResponseDto> getCompleteBoardList() {

        List<BoardList> boardLists = boardRepository.findAll();

        return boardLists.stream().map(ThumbnailResponseDto::new).filter(b -> b.getQuestEnum().equals(COMPLETE))
                .limit(8).collect(Collectors.toList());
    }

    @Transactional
    public UploadFileResponse createBoard(List<MultipartFile> image, BoardRequestDto boardListDto,
                                          HttpServletRequest request) {

        String token = jwtTokenProvider.resolveAccessToken(request);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        boardListDto.setUser(user);

        BoardList boardList = boardListDto.toEntity();
        boardRepository.save(boardList);

        List<String> downloadLink = uploadBoardListFile(image, boardList);
        List<String> downloadUri = new ArrayList<>();

        for(String Link : downloadLink) {
            File file = fileRepository.findByFileUrl(Link);
            downloadUri.add(file.getFileName());
        }

        UploadFileResponse uploadFileResponse = new UploadFileResponse(boardList.getId(), downloadUri);

        return uploadFileResponse;
    }

    private List<String> uploadBoardListFile(List<MultipartFile> image, BoardList boardList) {
        return image.stream()
                .map(file -> s3UploadService.uploadFile(file))
                .map(url -> createFile(boardList, url))
                .map(file -> file.getFileUrl())
                .collect(Collectors.toList());
    }

    private File createFile(BoardList boardList, String url) {
        return fileRepository.save(File.builder()
                .fileUrl(url)
                .fileName(StringUtils.getFilename(url))
                .boardList(boardList)
                .build());
    }

    @Transactional
    public UploadFileResponse updateBoard(BoardUpdateRequestDto boardListDto, HttpServletRequest request) {

        String token = jwtTokenProvider.resolveAccessToken(request);
        String email = jwtTokenProvider.getUserEmail(token);

        User user = userRepository.findByEmail(email).orElseThrow(() ->
        { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        BoardList boardList = boardRepository.findById(boardListDto.getId()).orElseThrow(() ->
            { throw new UnAuthorizedException("E0002", ACCESS_DENIED_EXCEPTION); });

        if (!boardList.getUser().getNickname().equals(user.getNickname())) {
            throw new UnAuthorizedException("NOT_FOUND_POST", ACCESS_DENIED_EXCEPTION);
        }

        validateDeletedFiles(boardListDto);
        uploadFiles(boardListDto, boardList);

        boardList.update(boardListDto);

        List<String> downloadUri = new ArrayList<>();

        for (MultipartFile Link : boardListDto.getImage()) {
            downloadUri.add(Link.getOriginalFilename());
        }

        UploadFileResponse uploadFileResponse = new UploadFileResponse(boardListDto.getId(), downloadUri);

        return uploadFileResponse;
    }

    private void validateDeletedFiles(BoardUpdateRequestDto boardListDto) {
        fileRepository.findBySavedFileUrl(boardListDto.getId()).stream()
                .filter(file -> !boardListDto.getSavedFileUrl().stream().anyMatch(Predicate.isEqual(file.getFileUrl())))
                .forEach(url -> {
                    fileRepository.delete(url);
                    s3UploadService.deleteFile(url.getFileUrl());
                });
    }

    private void uploadFiles(BoardUpdateRequestDto boardListDto, BoardList boardList) {
        boardListDto.getImage()
                .stream()
                .forEach(file -> {
                    String url = s3UploadService.uploadFile(file);
                    createFile(boardList, url);
                });
    }

    @Transactional
    public void deleteBoard(Long id, HttpServletRequest request) {

        String token = jwtTokenProvider.resolveAccessToken(request);
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
}
