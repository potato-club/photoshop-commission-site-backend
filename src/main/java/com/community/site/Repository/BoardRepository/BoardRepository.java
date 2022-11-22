package com.community.site.Repository.BoardRepository;

import com.community.site.entity.BoardList;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardList, Long> {
    Optional<BoardList> findByNickname(String nickname);
    Optional<BoardList> findByTitle(String title);
    Optional<BoardList> findById(Long id);

    @EntityGraph(attributePaths = {"comments", "comments.parent"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("select distinct b from BoardList b")
    List<BoardList> findAll();
}
