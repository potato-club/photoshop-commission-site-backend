package com.community.site.dto.CommentDto;

import com.community.site.entity.BoardList;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestDto {

    @ApiModelProperty(value="부모 댓글 Id", example = "1", required = true)
    private Long parentId;

    @ApiModelProperty(value="댓글 내용", example = "재밌당", required = true)
    @NotBlank(message = "댓글은 1자 이상 100자 이하여야 합니다.")
    private String comment;

    @ApiModelProperty(value="생성 시간", example = "yyyy.MM.dd HH:mm", hidden = true)
    private String createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

    @ApiModelProperty(value="수정 시간", example = "yyyy.MM.dd HH:mm", hidden = true)
    private String modifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

    @ApiModelProperty(value="게시글 정보", example = "board_list_id", hidden = true)
    private BoardList boardList;

}

