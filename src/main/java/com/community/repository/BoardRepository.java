package com.community.repository;

import com.community.entity.BoardList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardList, Long> {
    Page<BoardList> findAllByType(Pageable pageable, String type);
}
