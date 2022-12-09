package com.community.site.enumcustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageOpen {
    OPEN("ROLE_OPEN", "공개"),
    NOT_OPEN("ROLE_NOT_OPEN", "비공개");

    private final String key;
    private final String title;
}
