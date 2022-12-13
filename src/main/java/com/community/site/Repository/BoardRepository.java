package com.community.site.Repository;

import com.community.site.entity.BoardList;
import com.community.site.entity.User;
import com.community.site.enumcustom.BoardEnumCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardList, Long> {
    Page<BoardList> findByNickname(String nickname, Pageable pageable);
    Page<BoardList> findByTitle(String title, Pageable pageable);
    Optional<BoardList> findById(Long id);
    Page<BoardList> findAllByQuestEnum(BoardEnumCustom questEnum, Pageable pageable);
    Page<BoardList> findAllByUser(User user, Pageable pageable);
    List<BoardList> findByQuestEnum(BoardEnumCustom questEnum);
    List<BoardList> findByUser(User user);
    Page<BoardList> findBySelectedArtist(User user, Pageable pageable);
}
