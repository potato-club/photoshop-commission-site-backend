package com.community.site.controller;

import com.community.site.dto.UserDto.UserNicknameDto;
import com.community.site.service.MissionService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "localhost:3000")
@Api(tags = {"의뢰 Controller"})
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/list/{id}/request")
    public List<UserNicknameDto> getRequestUserList(@PathVariable Long id) {
        return missionService.getRequestUserList(id);
    }

    @PostMapping("/list/{id}/request")
    public ResponseEntity<String> addRequestUser(@PathVariable Long id, HttpServletRequest request) {
        missionService.addRequestUser(id, request);
        return ResponseEntity.ok("리스트에 등록됨");
    }

    @PostMapping("/list/{id}/artist")
    public ResponseEntity<String> setBoardArtist(@PathVariable Long id, @RequestBody UserNicknameDto artistDto) {
        String artistNickname = missionService.setBoardArtist(id, artistDto);
        return ResponseEntity.ok(artistNickname);
    }
}
