package com.community.site.dto.CommentDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDeleteRequestDto {

    @ApiModelProperty(value="댓글 번호", example = "1", required = true)
    private Long id;
}
