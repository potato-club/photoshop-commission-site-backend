package com.community.entity;

import com.nimbusds.oauth2.sdk.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(BaseEntity.class)
@ToString(of = {"id", "email"})
@Table(name = "member")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToOne(fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    @Enumerated(STRING)
    private Role role;

    @Embedded
    private BaseEntity timeEntity;

    @Builder
    public User(String email, UserProfile userProfile, Role role, BaseEntity timeEntity){
        this.email = email;
        this.userProfile = userProfile;
        this.role = role;
        this.timeEntity = timeEntity;
    }

    public static User createUser(String email, String nickname, String provider, String providerId, String imageUrl) {

        UserProfile profile = UserProfile.createProfile(nickname, provider, providerId, imageUrl);

        User user = User.builder()
                .email(email)
                .role(Role.USER)
                .build();

        user.addUserProfile(profile);

        return user;
    }
}
