package com.community.site.entity.Redis;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    private String refreshToken;
}
