package com.community.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(of = {"id", "nickName", "imageUrl"})
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

    public void setUser(User user) {
        this.user = user;
    }

    @Builder
    public UserProfile(String nickname, String provider, String providerId, String imageUrl) {
        this.nickName = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.imageUrl = imageUrl;
    }

    @Builder
    public static UserProfile createProfile(String nickname, String provider, String providerId, String imageUrl) {
        return  UserProfile.builder()
                .nickname(nickname)
                .imageUrl(imageUrl)
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
