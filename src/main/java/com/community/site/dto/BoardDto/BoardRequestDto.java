package com.community.site.dto.BoardDto;

import com.community.site.entity.User;
import com.community.site.enumcustom.BoardEnumCustom;
import com.community.site.entity.BoardList;
import com.community.site.enumcustom.ImageOpen;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequestDto {

    @ApiModelProperty(value="게시글 번호", example = "1", hidden = true)
    private Long id;

    @ApiModelProperty(value="생성 시간", example = "yyyy.MM.dd", hidden = true)
    private String createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

    @ApiModelProperty(value="수정 시간", example = "yyyy.MM.dd", hidden = true)
    private String modifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

    @ApiModelProperty(value="게시글 제목", example = "테스트 제목입니다.", required = true)
    private String title;

    @ApiModelProperty(value="글 공개 상태", example = "OPEN", hidden = true)
    private ImageOpen imageOpen;

    @ApiModelProperty(value="의뢰 상태", example = "BEFORE", hidden = true)
    private BoardEnumCustom questEnum;

    @ApiModelProperty(value="게시글 내용", example = "테스트 글입니다.", required = true)
    private String context;

    @ApiModelProperty(value="사용자 정보", hidden = true)
    private User user;

    public BoardList toEntity() {
        BoardList boardList = BoardList.builder()
                .id(id)
                .createdDate(createdDate)
                .modifiedDate(modifiedDate)
                .title(title)
                .nickname(user.getNickname())
                .imageOpen(imageOpen)
                .questEnum(questEnum)
                .context(context)
                .user(user)
                .build();

        return boardList;
    }
}
