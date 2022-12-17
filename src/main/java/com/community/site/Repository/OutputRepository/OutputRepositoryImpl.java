package com.community.site.Repository.OutputRepository;

import com.community.site.entity.Output;
import com.community.site.entity.QBoardList;
import com.community.site.entity.QOutput;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OutputRepositoryImpl implements OutputRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Output> findBySavedOutputUrl(Long id) {
        return queryFactory
                .selectFrom(QOutput.output)
                .innerJoin(QOutput.output.boardList, QBoardList.boardList)
                .where(QBoardList.boardList.id.eq(id))
                .fetch();
    }
}
