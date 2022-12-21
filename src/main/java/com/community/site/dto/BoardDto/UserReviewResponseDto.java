package com.community.site.dto.BoardDto;

import com.community.site.entity.BoardList;
import lombok.Getter;

@Getter
public class UserReviewResponseDto {
    private String title;
    private String selectedArtistNickname;
    private String createdDate;

    public UserReviewResponseDto(BoardList boardList) {
        this.title = boardList.getTitle();
        this.selectedArtistNickname = boardList.getSelectedArtist().getNickname();
        this.createdDate = boardList.getCreatedDate();
    }
}
