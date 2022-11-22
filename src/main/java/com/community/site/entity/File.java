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
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardList_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BoardList boardList;

    @Builder
    public File(String fileName, String fileUrl, BoardList boardList) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.boardList = boardList;
    }
}
