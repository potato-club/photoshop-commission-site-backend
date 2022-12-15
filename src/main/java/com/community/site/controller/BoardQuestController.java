package com.community.site.controller;

import com.community.site.dto.UserDto.UserNicknameDto;
import com.community.site.service.BoardQuestService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "localhost:3000")
@Api(tags = {"의뢰 Controller"})
public class BoardQuestController {
    private final BoardQuestService boardQuestService;

    @GetMapping("/list/{id}/request")
    public Page<UserNicknameDto> getRequestUserList(@PathVariable Long id, @RequestParam("page") int page,
                                                    HttpServletRequest request, HttpServletResponse response) {
        return boardQuestService.getRequestUserList(id, page, request, response);
    }

    @PostMapping("/list/{id}/request")
    public ResponseEntity<String> acceptQuest(@PathVariable Long id, HttpServletRequest request,
                                              HttpServletResponse response) {
        boardQuestService.acceptQuest(id, request, response);
        return ResponseEntity.ok("리스트에 등록됨");
    }

    @PutMapping("/list/{id}/request")
    public ResponseEntity<String> exceptQuest(@PathVariable Long id, HttpServletRequest request,
                                              HttpServletResponse response) {
        boardQuestService.exceptQuest(id, request, response);
        return ResponseEntity.ok("리스트에서 제거됨");
    }

    @PostMapping("/list/{id}/artist")
    public ResponseEntity<String> chooseArtist(@PathVariable Long id, @RequestBody UserNicknameDto artistDto,
                                               HttpServletRequest request, HttpServletResponse response) {
        boardQuestService.chooseArtist(id, artistDto, request, response);
        return ResponseEntity.ok("ARTIST 선택 완료");
    }
}
