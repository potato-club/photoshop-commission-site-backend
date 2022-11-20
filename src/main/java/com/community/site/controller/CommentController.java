package com.community.site.controller;

import com.community.site.dto.CommentDto.CommentDeleteRequestDto;
import com.community.site.dto.CommentDto.CommentResponseDto;
import com.community.site.dto.CommentDto.CommentUpdateRequestDto;
import com.community.site.service.CommentService;
import com.community.site.dto.CommentDto.CommentRequestDto;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@CrossOrigin(origins = "localhost:3000")
@Api(tags = {"댓글 Controller"})
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/list/{id}/comments/parent")
    public ResponseEntity<String> createParentComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto,
                                        @ApiIgnore HttpServletRequest request) {
        return ResponseEntity.ok(commentService.createParentComment(id, commentRequestDto, request));
    }

    @PostMapping("/list/{id}/comments/child")
    public ResponseEntity<String> createChildComment(@RequestBody CommentRequestDto commentRequestDto,
                                                @ApiIgnore HttpServletRequest request) {
        return ResponseEntity.ok(commentService.createChildComment(commentRequestDto, request));
    }

    @GetMapping("/list/{id}/comments")
    public List<CommentResponseDto> readComment(@PathVariable Long id) {
        return commentService.readComment(id);
    }

    @PutMapping("/list/{id}/comments")
    public ResponseEntity<String> updateComment(@RequestBody CommentUpdateRequestDto commentRequestDto,
                                        HttpServletRequest request) {
        commentService.updateComment(commentRequestDto, request);
        return ResponseEntity.ok("댓글 업데이트 성공");
    }

    @DeleteMapping("/list/{id}/comments")
    public ResponseEntity<String> delete(@RequestBody CommentDeleteRequestDto requestDto,
            HttpServletRequest request) {
        commentService.deleteComment(requestDto, request);
        return ResponseEntity.ok("댓글 삭제 완료");
    }
}
