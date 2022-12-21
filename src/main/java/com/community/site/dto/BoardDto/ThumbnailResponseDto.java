package com.community.site.dto.BoardDto;

import com.community.site.dto.FileDto.FileResponseDto;
import com.community.site.enumcustom.BoardEnumCustom;
import com.community.site.entity.BoardList;
import com.community.site.enumcustom.ImageOpen;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.community.site.enumcustom.ImageOpen.OPEN;

@Getter
public class ThumbnailResponseDto {
    private Long id;

    @JsonFormat(pattern = "yyyy.MM.dd")
    private String createdDate;

    private String nickname;
    private String title;
    private ImageOpen imageOpen;
    private BoardEnumCustom questEnum;
    private List<FileResponseDto> image;

    public ThumbnailResponseDto (BoardList boardList) {
        this.id = boardList.getId();
        this.nickname = boardList.getNickname();
        this.title = boardList.getTitle();
        this.imageOpen = boardList.getImageOpen();
        this.questEnum = boardList.getQuestEnum();
        if (boardList.getImageOpen() == OPEN) {
            this.image = boardList.getImage().stream().map(FileResponseDto::new).limit(1).collect(Collectors.toList());
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(boardList.getCreatedDate(), inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.createdDate = dateTime.format(outputFormatter);
    }
}
