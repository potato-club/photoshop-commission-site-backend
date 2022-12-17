package com.community.site.dto.FileDto;

import com.community.site.entity.Output;
import lombok.Getter;

@Getter
public class OutputResponseDto {

    private String fileName;
    private String fileUrl;

    public OutputResponseDto(Output output) {
        this.fileName = output.getFileName();
        this.fileUrl = output.getFileUrl();
    }
}
