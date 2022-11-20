package com.community.site.dto.BoardDto;

import com.community.site.dto.FileDto.FileResponseDto;
import com.community.site.enumcustom.BoardEnumCustom;
import com.community.site.entity.BoardList;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ThumbnailResponseDto {
    private Long id;
    private String createdDate;
    private String nickname;
    private String title;
    private BoardEnumCustom questEnum;
    private List<FileResponseDto> image;

    public ThumbnailResponseDto (BoardList boardList) {
        this.id = boardList.getId();
        this.createdDate = boardList.getCreatedDate();
        this.nickname = boardList.getNickname();
        this.title = boardList.getTitle();
        this.questEnum = boardList.getQuestEnum();
        this.image = boardList.getImage().stream().map(FileResponseDto::new).limit(1).collect(Collectors.toList());
    }
}
