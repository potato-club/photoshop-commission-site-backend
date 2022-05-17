package com.community.controller;

import com.community.controller.config.oauth.AuthorizationExtractor;
import com.community.dto.CustomResponse;
import com.community.dto.LoginResponse;
import com.community.service.OauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class OauthController {

    private final OauthService oauthService;

    @GetMapping("/login/kakao/{provider}")
    public ResponseEntity<LoginResponse> login(@PathVariable String provider, @RequestParam String code) {
        LoginResponse loginResponse = oauthService.login(provider, code);

        return ResponseEntity.ok().body(loginResponse);
    }

    @GetMapping("/oauth/logout?client_id=${spring.security.oauth2.client.registration.kakao.client-id}" +
            "&logout_redirect_uri=${spring.security.oauth2.client.registration.kakao.logout-redirect-uri}")
    public void logout(HttpServletRequest request) {
        AuthorizationExtractor.extract(request);
    }
}
