package com.community.site.dto.BoardDto;

import com.community.site.dto.CommentDto.CommentResponseDto;
import com.community.site.dto.FileDto.FileResponseDto;
import com.community.site.entity.BoardList;
import com.community.site.enumcustom.BoardEnumCustom;
import com.community.site.enumcustom.ImageOpen;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static com.community.site.enumcustom.ImageOpen.OPEN;

@Getter
public class BoardResponseDto {

    private Long id;

    @JsonFormat(pattern = "yyyy.MM.dd")
    private String createdDate;

    @JsonFormat(pattern = "yyyy.MM.dd")
    private String modifiedDate;

    private String nickname;
    private String title;
    private ImageOpen imageOpen;
    private BoardEnumCustom questEnum;
    private String context;
    private List<CommentResponseDto> comments;
    private List<FileResponseDto> image;

    public BoardResponseDto(BoardList boardList, String nickname) {
        this.id = boardList.getId();
        this.createdDate = boardList.getCreatedDate();
        this.modifiedDate = boardList.getModifiedDate();
        this.nickname = boardList.getNickname();
        this.title = boardList.getTitle();
        this.imageOpen = boardList.getImageOpen();
        this.questEnum = boardList.getQuestEnum();
        this.context = boardList.getContext();
        this.comments = boardList.getComments().stream().map(CommentResponseDto::new)
                .filter(comments -> comments.isParent()).collect(Collectors.toList());
        if (boardList.getImageOpen() == OPEN || boardList.getNickname().equals(nickname)) {
            this.image = boardList.getImage().stream().map(FileResponseDto::new).collect(Collectors.toList());
        } else if(boardList.getSelectedArtist() != null && boardList.getSelectedArtist().getNickname().equals(nickname)) {
            this.image = boardList.getImage().stream().map(FileResponseDto::new).collect(Collectors.toList());
        }
    }
}
