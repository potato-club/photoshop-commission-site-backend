package com.community.site.dto.UserDto;

import com.community.site.entity.User;
import com.community.site.enumcustom.UserRole;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class UserResponseDto implements Serializable {

    private String nickname;
    private String email;
    private String introduction;
    private UserRole userRole;
    private String createdDate;
    private String grade;

    public UserResponseDto(User user) {
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.introduction = user.getIntroduction();
        this.userRole = user.getUserRole();
        this.createdDate = String.valueOf(user.getCreatedDate());
        this.grade =  String.format("%.1f", user.getGrade());
    }
}
