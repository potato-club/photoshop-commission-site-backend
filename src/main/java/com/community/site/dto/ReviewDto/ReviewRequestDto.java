package com.community.site.dto.ReviewDto;

import com.community.site.entity.BoardList;
import com.community.site.entity.Review;
import com.community.site.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {

    @ApiModelProperty(value="게시글 id", example = "1", required = true)
    private Long roomId;

    @ApiModelProperty(value="후기 글", example = "후기 블라블라~", required = true)
    private String content;

    @ApiModelProperty(value="평점", example = "4.3", required = true)
    private Double grade;

    @ApiModelProperty(value="의뢰자 닉네임", hidden = true)
    private String nickname;

    @ApiModelProperty(value="후기 작성 시간", hidden = true)
    private String createdDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

    @ApiModelProperty(value="게시글", hidden = true)
    private BoardList boardList;

    @ApiModelProperty(value="아티스트 정보", hidden = true)
    private User user;

    public Review toEntity() {
        return Review.builder()
                .nickname(nickname)
                .content(content)
                .grade(grade)
                .createdDate(createdDate)
                .boardList(boardList)
                .user(user)
                .build();
    }
}
