package com.community.site.controller;

import com.community.site.dto.CommentDto.*;
import com.community.site.service.CommentService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@Api(tags = {"댓글 Controller"})
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/list/{id}/comments/parent")
    public ResponseEntity<String> createParentComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto,
                                        @ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response) {
        return ResponseEntity.ok(commentService.createParentComment(id, commentRequestDto, request, response));
    }

    @PostMapping("/list/{id}/comments/child")
    public ResponseEntity<String> createChildComment(@RequestBody CommentChildRequestDto commentChildRequestDto,
                                                     @ApiIgnore HttpServletRequest request,
                                                     @ApiIgnore HttpServletResponse response) {
        return ResponseEntity.ok(commentService.createChildComment(commentChildRequestDto, request, response));
    }

    @GetMapping("/list/{id}/comments")
    public List<CommentResponseDto> readComment(@PathVariable Long id) {
        return commentService.readComment(id);
    }

    @PutMapping("/list/{id}/comments")
    public ResponseEntity<String> updateComment(@RequestBody CommentUpdateRequestDto commentRequestDto,
                                        @ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response) {
        commentService.updateComment(commentRequestDto, request, response);
        return ResponseEntity.ok("댓글 업데이트 성공");
    }

    @DeleteMapping("/list/{id}/comments")
    public ResponseEntity<String> delete(@RequestBody CommentDeleteRequestDto requestDto,
            @ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response) {
        commentService.deleteComment(requestDto, request, response);
        return ResponseEntity.ok("댓글 삭제 완료");
    }
}
