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
    List<BoardList> findByNickname(String nickname);
    List<BoardList> findByTitle(String title);
    Optional<BoardList> findById(Long id);
    List<BoardList> findAllByQuestEnum(BoardEnumCustom questEnum);
    List<BoardList> findAllByUser(User user);
    List<BoardList> findByQuestEnum(BoardEnumCustom questEnum);
    List<BoardList> findByUser(User user);
    List<BoardList> findBySelectedArtist(User user);
}
