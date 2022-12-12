package com.community.site.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    @CreatedDate
    private String createdDate;

    @Column(nullable = false)
    private String nickname;    // 의뢰자 닉네임

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Double grade;

    @OneToOne
    @JoinColumn(name = "linked_board_list")
    private BoardList boardList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_artist")
    private User user;          // 아티스트 유저 정보
}
