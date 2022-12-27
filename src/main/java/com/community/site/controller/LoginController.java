package com.community.site.controller;

import com.community.site.dto.JwtDto.TokenResponse;
import com.community.site.dto.UserDto.TestRequestDto;
import com.community.site.dto.UserDto.UserMyPageRequestDto;
import com.community.site.dto.UserDto.UserRequestDto;
import com.community.site.dto.UserDto.UserResponseDto;
import com.community.site.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = {"로그인 Controller"})
public class LoginController {

    private final LoginService loginService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginUser", value = "로그인 세션값", required = true,
                    dataType = "Object", paramType = "query")
    })
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "카카오 인증코드", required = true,
                    dataType = "String", paramType = "query")
    })
    @GetMapping("/check/user")
    public MultiValueMap<String, Object> checkUser(@RequestParam String code, HttpServletResponse response) {
        return loginService.checkUser(code, response);
    }


    @PostMapping("/signup")
    public MultiValueMap<String, Object> signUp(@RequestBody UserRequestDto userDto, HttpServletResponse response) {
        return loginService.signUp(userDto, response);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "nickname", value = "nickname 값", required = true,
                    dataType = "String", paramType = "query")
    })
    @GetMapping("/signup/checkbox")
    public boolean checkNickname(String nickname) {
        return loginService.checkNickname(nickname);
    }

    @PostMapping("/create/token")
    public TokenResponse createToken(@RequestBody TestRequestDto testRequestDto, HttpServletResponse response) {
        return loginService.createToken(testRequestDto, response);
    }

    @PutMapping("/resolver/token")
    public String resolverToken(@RequestBody UserMyPageRequestDto requestDto, HttpServletRequest request,
                                HttpServletResponse response) {
        return loginService.resolverToken(requestDto, request, response);
    }
}
