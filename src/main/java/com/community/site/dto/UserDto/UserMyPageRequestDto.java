package com.community.site.dto.UserDto;

import com.community.site.entity.User;
import com.community.site.enumcustom.UserRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMyPageRequestDto {

    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,8}$", message = "닉네임은 특수문자를 제외한 2~8자리여야 합니다.")
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @ApiModelProperty(value="닉네임", example = "홍길동", required = true)
    private String nickname;

    @ApiModelProperty(value="자기 소개", example = "포토샵 장인입니다.", required = true)
    private String introduction;

    @ApiModelProperty(value="유저 역할", example = "ARTIST", required = true)
    private UserRole userRole;

    @ApiModelProperty(value="수정 시간", example = "yyyy.MM.dd", hidden = true)
    private String modifiedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

    public User toEntity() {
        User user = User.builder()
                .nickname(nickname)
                .introduction(introduction)
                .userRole(userRole)
                .modifiedDate(modifiedDate)
                .build();

        return user;
    }
}
