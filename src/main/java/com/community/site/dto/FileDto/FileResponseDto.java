package com.community.site.dto.FileDto;

import com.community.site.entity.File;
import lombok.Getter;

@Getter
public class FileResponseDto {
    private String fileName;
    private String fileUrl;
    private Long boardListId;

    public FileResponseDto(File file) {
        this.fileName = file.getFileName();
        this.fileUrl = file.getFileUrl();
        this.boardListId = file.getBoardList().getId();
    }
}
