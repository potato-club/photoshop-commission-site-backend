package com.community.site.enumcustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반 사용자"),
    ARTIST("ROLE_ARTIST", "아티스트"),
    ME("ROLE_ME", "자신"),
    ADMINISTRATOR("ROLE_ADMINISTRATOR", "관리자");

    private final String key;
    private final String title;
}
