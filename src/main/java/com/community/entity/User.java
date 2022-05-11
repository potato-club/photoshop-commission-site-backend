package com.community.entity;

import com.community.constant.Role;
import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
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

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<BoardList> boardList = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
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

    @Builder
    public static User createUser(String email, String nickname, String provider, String providerId, String imageUrl) {

        UserProfile profile = UserProfile.createProfile(nickname, provider, providerId, imageUrl);

        User user = User.builder()
                .email(email)
                .role(Role.USER)
                .build();

        user.addUserProfile(profile);

        return user;
    }

    public void addUserProfile(UserProfile userProfile){
        this.userProfile = userProfile;
        userProfile.setUser(this);
    }
}
