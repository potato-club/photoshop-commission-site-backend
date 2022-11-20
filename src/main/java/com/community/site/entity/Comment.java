package com.community.site.entity;

import com.community.site.dto.CommentDto.CommentRequestDto;
import com.community.site.dto.CommentDto.CommentUpdateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "comments")
@Entity
@BatchSize(size = 10)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @Column(name = "created_date")
    @CreatedDate
    private String createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private String modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_list_id")
    private BoardList boardList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Comment(User user, BoardList boardList, String comment, CommentRequestDto requestDto, Comment parent) {
        this.user = user;
        this.boardList = boardList;
        this.comment = comment;
        this.parent = parent;
        this.createdDate = requestDto.getCreatedDate();
        this.modifiedDate = requestDto.getModifiedDate();
    }

    public void update(CommentUpdateRequestDto requestDto) {
        this.comment = requestDto.getComment();
        this.modifiedDate = requestDto.getModifiedDate();
    }

    public static Comment parent(User user, BoardList boardList, String comment, CommentRequestDto requestDto) {
        return new Comment(user, boardList, comment, requestDto, null);
    }

    public static Comment child(User user, BoardList boardList, String comment, CommentRequestDto requestDto ,Comment parent) {
        Comment child = new Comment(user, boardList, comment, requestDto, parent);
        parent.getChildren().add(child);
        return child;
    }

    public boolean isParent() {
        return Objects.isNull(parent);
    }
}
