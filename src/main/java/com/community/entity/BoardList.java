package com.community.entity;

import com.community.constant.BoardRequestType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "boardlist")
@DynamicInsert
@DynamicUpdate
@Getter @Setter
public class BoardList extends BaseTimeEntity {    // 카카오톡 로그인을 구현할 예정이기 때문에 닉네임은 따로 만들지 않음.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardlist_id")
    private Long id;                            // 게시글 고유 ID, Auto_increment

    @Column(name = "request_type")
    @Enumerated(EnumType.STRING)
    private BoardRequestType boardRequestType;  // 의뢰 전, 의뢰 중, 의뢰 완료

    @Column(name = "board_type")
    private String type;                        // 게시판 카테고리(직접 할당)

    @Column(name = "title", length = 500, nullable = false)
    private String title;                       // 제목

    @Lob
    @Column(name = "contents_text", nullable = false)
    private String contentsText;                    // 의뢰 내용(문자)

    @Lob
    @Column(name = "contents_picture")
    private String contentsPicture;                 // 의뢰 내용(사진)

    @Column(name = "created_time")
    private LocalDateTime createdTime;          // 만든 날짜 및 시간

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;          // 수정된 날짜 및 시간

    @Column(name = "likes")
    private Integer likes;                      // 좋아요 수

    @Column(name = "counts")
    private Integer counts;                     // 조회 수

}
