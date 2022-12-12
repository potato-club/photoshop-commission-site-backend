package com.community.site.controller;

import com.community.site.dto.BoardDto.ThumbnailResponseDto;
import com.community.site.dto.ReviewDto.ReviewRequestDto;
import com.community.site.dto.ReviewDto.ReviewResponseDto;
import com.community.site.dto.UserDto.UserMyPageRequestDto;
import com.community.site.dto.UserDto.UserResponseDto;
import com.community.site.service.MyPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "localhost:3000")
@Api(tags = {"마이페이지 Controller"})
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/mypage")
    public UserResponseDto viewMyPage(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response) {
        return myPageService.viewMyPage(request, response);
    }

    @GetMapping("/mypage/review")
    public Page<ReviewResponseDto> viewReviewList(@ApiIgnore HttpServletRequest request,
                                                  @ApiIgnore HttpServletResponse response,
                                                  @RequestParam("page") int page) {
        return myPageService.viewReviewList(request, response, page);
    }

    @GetMapping("/mypage/grade")
    public String averageGrade(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response) {
        return myPageService.averageGrade(request, response);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "userDto", value = "업데이트 값", required = true,
                    dataType = "Object", paramType = "query")
    })
    @PutMapping("/mypage/update")
    public ResponseEntity<String> updateMyPage(@RequestBody UserMyPageRequestDto userDto,
                                               @ApiIgnore HttpServletRequest request,
                                               @ApiIgnore HttpServletResponse response) {
        myPageService.updateMyPage(userDto, request, response);
        return ResponseEntity.ok("회원정보가 수정되었습니다.");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "requestDto", value = "후기 작성 값", required = true,
                    dataType = "Object", paramType = "query")
    })
    @PostMapping("/mypage/review/write")
    public ResponseEntity<String> writeReviewAndGrade(@RequestBody ReviewRequestDto requestDto,
                                                      @ApiIgnore HttpServletRequest request,
                                                      @ApiIgnore HttpServletResponse response) {
        myPageService.writeReviewAndGrade(requestDto, request, response);
        return ResponseEntity.ok("후기 작성이 완료되었습니다.");
    }


    @DeleteMapping("/mypage/resign")
    public ResponseEntity<String> deleteUser(@ApiIgnore HttpServletRequest request,
                                             @ApiIgnore HttpServletResponse response) {
        myPageService.resign(request, response);
        return ResponseEntity.ok("회원탈퇴 처리 되었습니다.");
    }
}
