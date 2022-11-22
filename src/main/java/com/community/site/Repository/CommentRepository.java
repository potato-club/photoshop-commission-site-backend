package com.community.site.Repository;

import com.community.site.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardListIdAndParentIsNull(Long id);
}
