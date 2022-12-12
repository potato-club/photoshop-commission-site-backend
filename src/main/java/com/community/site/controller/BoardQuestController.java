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
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "localhost:3000")
@Api(tags = {"의뢰 Controller"})
public class BoardQuestController {
    private final BoardQuestService boardQuestService;

    @GetMapping("/list/{id}/request")
    public Page<UserNicknameDto> getRequestUserList(@PathVariable Long id, @RequestParam("page") int page, HttpServletRequest request) {
        return boardQuestService.getRequestUserList(id, page, request);
    }

    @PostMapping("/list/{id}/request")
    public ResponseEntity<String> acceptQuest(@PathVariable Long id, HttpServletRequest request) {
        boardQuestService.acceptQuest(id, request);
        return ResponseEntity.ok("리스트에 등록됨");
    }

    @PostMapping("/list/{id}/artist")
    public ResponseEntity<String> chooseArtist(@PathVariable Long id, @RequestBody UserNicknameDto artistDto,
                                               HttpServletRequest request) {
        boardQuestService.chooseArtist(id, artistDto, request);
        return ResponseEntity.ok("ARTIST 선택 완료");
    }
}
