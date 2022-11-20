package com.community.site.enumcustom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardEnumCustom {

    BEFORE("ROLE_BEFORE", "의뢰 전"),
    REQUESTING("ROLE_REQUESTING", "의뢰 중"),
    COMPLETE("ROLE_COMPLETE", "의뢰 완료");

    private final String key;
    private final String title;
}
