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
    List<BoardList> findByNicknameContaining(String nickname, Pageable pageable);
    List<BoardList> findByTitleContaining(String title, Pageable pageable);
    Optional<BoardList> findById(Long id);
    List<BoardList> findAllByQuestEnum(BoardEnumCustom questEnum, Pageable pageable);
    List<BoardList> findByUserAndQuestEnum(User user, BoardEnumCustom boardEnumCustom);
    List<BoardList> findByQuestEnum(BoardEnumCustom questEnum);
    List<BoardList> findByUser(User user);
    List<BoardList> findBySelectedArtist(User user);
    long countByQuestEnum(BoardEnumCustom questEnum);
    long countByNicknameContaining(String keyword);
    long countByTitleContaining(String keyword);
    long countByUser(User user);
    long countBySelectedArtist(User user);
    long countByUserAndQuestEnum(User user, BoardEnumCustom boardEnumCustom);
}
