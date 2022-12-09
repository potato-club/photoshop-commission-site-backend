package com.community.site.controller;

import com.community.site.enumcustom.UserRole;
import com.community.site.service.TokenService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "localhost:3000")
@Api(tags = {"토큰 검증 및 재발급 Controller"})
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/check/writer")
    public boolean checkWriter(@RequestParam Long id,
                               @ApiIgnore HttpServletRequest request,
                               @ApiIgnore HttpServletResponse response) {
        return tokenService.checkWriter(id, request, response);
    }

    @GetMapping("/check/enum")
    public UserRole checkEnum(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpServletResponse response) {
        return tokenService.checkEnum(request, response);
    }
}
