package com.community.site.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "files")
public class File {     // 게시글 사진 저장용 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_list_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BoardList boardList;

    @Builder
    public File(String fileName, String fileUrl, BoardList boardList) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.boardList = boardList;
    }
}
