package com.team.RecipeRadar.domain.admin.domain;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.admin.dao.BlackList;
import com.team.RecipeRadar.domain.admin.dto.BlackListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.admin.dao.QBlackList.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomBlackRepositoryImpl implements CustomBlackRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<BlackListDto> allBlackList(Long lastId, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        if(lastId != null){
            builder.and(blackList.id.lt(lastId));
        }
        List<BlackList> blackListList = jpaQueryFactory.select(blackList)
                .from(blackList)
                .where(builder)
                .orderBy(blackList.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext =false;

        List<BlackListDto> collect = blackListList.stream().map(b -> new BlackListDto(b.getId(), b.getEmail(), b.isBlack_check())).collect(Collectors.toList());

        if (collect.size() > pageable.getPageSize()){
            collect.remove(pageable.getPageSize());
            hasNext= true;
        }

        return  new SliceImpl<>(collect,pageable,hasNext);

    }
}
