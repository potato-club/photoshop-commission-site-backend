package com.community.site.dto.BoardDto;

import com.community.site.dto.FileDto.FileResponseDto;
import com.community.site.dto.FileDto.OutputResponseDto;
import com.community.site.entity.BoardList;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static com.community.site.enumcustom.ImageOpen.OPEN;

@Getter
public class BoardOutputResponseDto {
    private List<OutputResponseDto> image;

    public BoardOutputResponseDto(BoardList boardList, String nickname) {
        if (boardList.getImageOpen() == OPEN || boardList.getNickname().equals(nickname)) {
            this.image = boardList.getOutputs().stream().map(OutputResponseDto::new).collect(Collectors.toList());
        } else if(boardList.getSelectedArtist() != null && boardList.getSelectedArtist().getNickname().equals(nickname)) {
            this.image = boardList.getOutputs().stream().map(OutputResponseDto::new).collect(Collectors.toList());
        }
    }
}
