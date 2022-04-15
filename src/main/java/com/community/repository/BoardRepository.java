package com.community.repository;

import com.community.entity.BoardList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardList, Long> {

}
