package com.community.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(of = {"id", "nickname", "imageUrl"})
public class UserProfile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_profile_id")
    private Long id;

    @Column(nullable = false)
    private String nickName;

    @OneToOne(mappedBy = "userProfile", fetch = LAZY)
    private User user;

    private String provider;
    private String providerId;

    private String imageUrl;

    public static UserProfile createProfile(String nickname, String provider, String providerId, String imageUrl) {
        return UserProfile.builder()
                .nickName(nickname)
                .imageUrl(imageUrl)
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
