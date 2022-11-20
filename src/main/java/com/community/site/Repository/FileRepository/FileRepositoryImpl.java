package com.community.site.Repository.FileRepository;

import com.community.site.entity.File;
import com.community.site.entity.QBoardList;
import com.community.site.entity.QFile;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<File> findBySavedFileUrl(Long id) {
        return queryFactory
                .selectFrom(QFile.file)
                .innerJoin(QFile.file.boardList, QBoardList.boardList)
                .where(QBoardList.boardList.id.eq(id))
                .fetch();
    }
}
