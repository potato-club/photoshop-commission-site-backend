package com.community.site.dto.BoardDto;

import com.community.site.dto.CommentDto.CommentResponseDto;
import com.community.site.dto.FileDto.FileResponseDto;
import com.community.site.entity.BoardList;
import com.community.site.enumcustom.BoardEnumCustom;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BoardResponseDto {

    private Long id;
    private String modifiedDate;
    private String nickname;
    private String title;
    private BoardEnumCustom questEnum;
    private String context;
    private Long userId;
    private List<CommentResponseDto> comments;
    private List<FileResponseDto> image;

    public BoardResponseDto(BoardList boardList) {
        this.id = boardList.getId();
        this.modifiedDate = boardList.getModifiedDate();
        this.nickname = boardList.getNickname();
        this.title = boardList.getTitle();
        this.questEnum = boardList.getQuestEnum();
        this.context = boardList.getContext();
        this.userId = boardList.getUser().getId();
        this.comments = boardList.getComments().stream().map(CommentResponseDto::new)
                .filter(comments -> !comments.getChildren().isEmpty()).collect(Collectors.toList());
        this.image = boardList.getImage().stream().map(FileResponseDto::new).collect(Collectors.toList());
    }
}
