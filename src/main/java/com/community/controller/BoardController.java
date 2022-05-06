package com.community.controller;

import com.community.entity.BoardList;
import com.community.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/list")
    public Page<BoardList> getAllBoardList(@PageableDefault(page = 0, size = 10, sort = "id",
            direction = Sort.Direction.DESC) Pageable pageable) {  // 게시글 전체 조회

        return boardService.getAllBoard(pageable);
    }

    @PostMapping("/create")
    public BoardList createBoard(@RequestBody BoardList boardList) {    // 게시글 생성
        return boardService.createBoardList(boardList);
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<BoardList> getBoardListById(@PathVariable Long id) {  // 게시글 상세 보기
        return boardService.getBoardList(id);
    }

    @PutMapping("/list/{id}")
    public ResponseEntity<BoardList> updateBoardListById(
            @PathVariable Long id, @RequestBody BoardList boardList) {  // 게시글 수정

        return boardService.updateBoardList(id, boardList);
    }

    @DeleteMapping("/list/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteBoardById(@PathVariable Long id) {
        return boardService.deleteBoardList(id);
    }
}
