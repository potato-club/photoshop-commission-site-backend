package com.community.site.dto.CommentDto;

import com.community.site.entity.Comment;
import lombok.Getter;

@Getter
public class MyCommentResponseDto {
    private String title;
    private String comment;
    private String modifiedDate;

    public MyCommentResponseDto(Comment comment) {
        this.title = comment.getBoardList().getTitle();
        this.comment = comment.getComment();
        this.modifiedDate = comment.getModifiedDate();
    }
}
