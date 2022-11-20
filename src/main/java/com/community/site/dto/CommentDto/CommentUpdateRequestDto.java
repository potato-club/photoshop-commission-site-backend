package com.community.site.dto.CommentDto;

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
public class CommentUpdateRequestDto {

    @ApiModelProperty(value="댓글 번호", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value="댓글 내용", example = "재밌당", required = true)
    @NotBlank(message = "댓글은 1자 이상 100자 이하여야 합니다.")
    private String comment;

    @ApiModelProperty(value="수정 시간", example = "yyyy.MM.dd HH:mm", hidden = true)
    private String modifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
}
