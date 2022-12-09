package com.community.site.dto.JwtDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckEnumRequest {

    @ApiModelProperty(value="게시글 ID", example = "1", required = true)
    private Long id;
}
