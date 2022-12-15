package com.community.site.dto.ReviewDto;

import com.community.site.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponseDto {
    private String nickname;
    private String content;
    private String grade;
    private String createdDate;

    public ReviewResponseDto(Review review) {
        this.nickname = review.getNickname();
        this.content = review.getContent();
        this.grade = String.format("%.1f", review.getGrade());
        this.createdDate = review.getCreatedDate();
    }
}
