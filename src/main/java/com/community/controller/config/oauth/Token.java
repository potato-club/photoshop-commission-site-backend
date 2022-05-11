package com.community.controller.config.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    private String value;
    private long expiredTime;

}