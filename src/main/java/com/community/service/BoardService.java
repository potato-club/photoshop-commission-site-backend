package com.community.service;

import com.community.controller.config.exception.ResourceNotFoundException;
import com.community.entity.BoardList;
import com.community.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public Page<BoardList> getAllBoard(Pageable pageable) {  // 저장된 게시글 전부 출력
        return boardRepository.findAll(pageable);
    }

    public BoardList createBoardList(BoardList boardList) { // 게시글 만들기
        return boardRepository.save(boardList);
    }

    public ResponseEntity<BoardList> getBoardList(Long id) {
        BoardList boardList = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not exist Board Data by id : ["+id+"]"));
        return ResponseEntity.ok(boardList);
    }

    public ResponseEntity<BoardList> updateBoardList(Long id, BoardList updatedBoard) {
        BoardList boardList = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not exist Board Data by id : ["+id+"]"));
        boardList.setType(updatedBoard.getType());
        boardList.setTitle(updatedBoard.getTitle());
        boardList.setContentsText(updatedBoard.getContentsText());
        boardList.setContentsPicture(updatedBoard.getContentsPicture());
        boardList.setUpdatedTime(LocalDateTime.now());

        BoardList endUpdatedBoard = boardRepository.save(boardList);
        return ResponseEntity.ok(endUpdatedBoard);
    }

    public ResponseEntity<Map<String, Boolean>> deleteBoardList(Long id) {
        BoardList boardList = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not exist Board Data by id : ["+id+"]"));

        boardRepository.delete(boardList);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Deleted Board Data by id : ["+id+"]", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

}
