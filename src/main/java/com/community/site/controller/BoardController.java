package com.community.site.controller;

import com.community.site.dto.BoardDto.*;
import com.community.site.enumcustom.ImageOpen;
import com.community.site.service.BoardService;
import com.community.site.service.S3.S3DownloadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = {"게시글 Controller"})
public class BoardController {

    private final BoardService boardService;
    private final S3DownloadService s3DownloadService;

    @GetMapping("/main/before/all")
    public Page<ThumbnailResponseDto> getAllBeforeBoardList(@RequestParam("page") int page) {
        return boardService.getAllBeforeBoardList(page);
    }

    @GetMapping("/main/requesting/all")
    public Page<ThumbnailResponseDto> getAllRequestingBoardList(@RequestParam("page") int page) {
        return boardService.getAllRequestingBoardList(page);
    }

    @GetMapping("/main/complete/all")
    public Page<ThumbnailResponseDto> getAllCompleteBoardList(@RequestParam("page") int page) {
        return boardService.getAllCompleteBoardList(page);
    }

    @GetMapping("/main/before")
    public List<ThumbnailResponseDto> getBeforeBoardList() {
        return boardService.getBeforeBoardList();
    }

    @GetMapping("/main/requesting")
    public List<ThumbnailResponseDto> getRequestingBoardList() {
        return boardService.getRequestingBoardList();
    }

    @GetMapping("/main/complete")
    public List<ThumbnailResponseDto> getCompleteBoardList() {
        return boardService.getCompleteBoardList();
    }

    @GetMapping("/list/{id}")
    public BoardResponseDto findBoardList(@PathVariable Long id,
                                          @ApiIgnore HttpServletRequest request,
                                          @ApiIgnore HttpServletResponse response) {
        return boardService.findBoardList(id, request, response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "title 값", required = true,
                    dataType = "String", paramType = "query")
    })
    @GetMapping("/filter/title")
    public Page<ThumbnailResponseDto> getTitleBoardList(@RequestParam String keyword, @RequestParam("page") int page) {
        return boardService.getTitleBoardList(keyword, page);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "nickname 값", required = true,
                    dataType = "String", paramType = "query")
    })
    @GetMapping("/filter/nickname")
    public Page<ThumbnailResponseDto> getNicknameBoardList(@RequestParam String keyword, @RequestParam("page") int page) {
        return boardService.getNicknameBoardList(keyword, page);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "image", value = "사진 저장용 배열", required = true,
                    dataType = "List<MultipartFile>", paramType = "query")
    })
    @PostMapping("/list/create")
    public ResponseEntity<String> createBoard(List<MultipartFile> image, ImageOpen imageOpen,
                                          BoardRequestDto boardListDto,
                                          @ApiIgnore HttpServletRequest request,
                                          @ApiIgnore HttpServletResponse response) {
        boardService.createBoard(image, imageOpen, boardListDto, request, response);
        return ResponseEntity.ok("게시글이 생성되었습니다.");
    }


    @PutMapping("/list/{id}")
    public ResponseEntity<String> updateBoard(@PathVariable Long id, BoardUpdateRequestDto boardListDto,
                                              ImageOpen imageOpen,
                                              @ApiIgnore HttpServletRequest request,
                                              @ApiIgnore HttpServletResponse response) {
        boardService.updateBoard(id, boardListDto, imageOpen, request, response);
        return ResponseEntity.ok("게시글이 수정되었습니다.");
    }


    @DeleteMapping("/list/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id, @ApiIgnore HttpServletRequest request,
                                              @ApiIgnore HttpServletResponse response) {
        boardService.deleteBoard(id, request, response);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "다운로드 받고 싶은 파일 이름", required = true,
                    dataType = "String", paramType = "query")
    })
    @GetMapping("/list/file/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam String fileName) {
        try{
            byte[] data = s3DownloadService.download(fileName);
            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\""
                            + URLEncoder.encode(fileName, "utf-8") + "\"")
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().contentLength(0).body(null);
        }
    }
}
