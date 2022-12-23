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
    List<BoardList> findByNicknameContainingOrderByIdDesc(String nickname, Pageable pageable);
    List<BoardList> findByTitleContainingOrderByIdDesc(String title, Pageable pageable);
    Optional<BoardList> findById(Long id);
    List<BoardList> findAllByQuestEnumOrderByIdDesc(BoardEnumCustom questEnum, Pageable pageable);
    List<BoardList> findByUserAndQuestEnumOrderByIdDesc(User user, BoardEnumCustom boardEnumCustom);
    List<BoardList> findByQuestEnumOrderByIdDesc(BoardEnumCustom questEnum);
    List<BoardList> findByUserOrderByIdDesc(User user, Pageable pageable);
    List<BoardList> findBySelectedArtistOrderByIdDesc(User user, Pageable pageable);
    long countByQuestEnum(BoardEnumCustom questEnum);
    long countByNicknameContaining(String keyword);
    long countByTitleContaining(String keyword);
    long countByUser(User user);
    long countBySelectedArtist(User user);
    long countByUserAndQuestEnum(User user, BoardEnumCustom boardEnumCustom);
}
