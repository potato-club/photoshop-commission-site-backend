package com.community.site.controller;

import com.community.site.dto.BoardDto.ThumbnailResponseDto;
import com.community.site.service.MyPageBoardService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "https://easyphoto.site")
@Api(tags = {"마이페이지 내 게시글 Controller"})
public class MyPageBoardController {

    private final MyPageBoardService myPageBoardService;

    @GetMapping("/mypage/before/all")
    public Page<ThumbnailResponseDto> getAllBeforeBoardList(@ApiIgnore HttpServletRequest request,
                                                            @ApiIgnore HttpServletResponse response,
                                                            @RequestParam("page") int page) {
        return myPageBoardService.myPageAllBeforeBoardList(request, response, page);
    }

    @GetMapping("/mypage/requesting/all")
    public Page<ThumbnailResponseDto> getAllRequestingBoardList(@ApiIgnore HttpServletRequest request,
                                                                @ApiIgnore HttpServletResponse response,
                                                                @RequestParam("page") int page) {
        return myPageBoardService.myPageAllRequestingBoardList(request, response, page);
    }

    @GetMapping("/mypage/complete/all")
    public Page<ThumbnailResponseDto> getAllCompleteBoardList(@ApiIgnore HttpServletRequest request,
                                                              @ApiIgnore HttpServletResponse response,
                                                              @RequestParam("page") int page) {
        return myPageBoardService.myPageAllCompleteBoardList(request, response, page);
    }

    @GetMapping("/mypage/before")
    public List<ThumbnailResponseDto> getBeforeBoardList(@ApiIgnore HttpServletRequest request,
                                                         @ApiIgnore HttpServletResponse response) {
        return myPageBoardService.myPageBeforeBoardList(request, response);
    }

    @GetMapping("/mypage/requesting")
    public List<ThumbnailResponseDto> getRequestingBoardList(@ApiIgnore HttpServletRequest request,
                                                             @ApiIgnore HttpServletResponse response) {
        return myPageBoardService.myPageRequestingBoardList(request, response);
    }

    @GetMapping("/mypage/complete")
    public List<ThumbnailResponseDto> getCompleteBoardList(@ApiIgnore HttpServletRequest request,
                                                           @ApiIgnore HttpServletResponse response) {
        return myPageBoardService.myPageCompleteBoardList(request, response);
    }
}
