package com.team.RecipeRadar.domain.balckLIst.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.balckLIst.domain.BlackList;
import com.team.RecipeRadar.domain.balckLIst.dto.BlackListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
;
import static com.team.RecipeRadar.domain.balckLIst.domain.QBlackList.blackList;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomBlackRepositoryImpl implements CustomBlackRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<BlackListDto> allBlackList(Long lastId, Pageable pageable) {

        BooleanBuilder builder = getBuilder(lastId);
        List<BlackList> blackListList = jpaQueryFactory.select(blackList)
                .from(blackList)
                .where(builder)
                .orderBy(blackList.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        List<BlackListDto> collectBlackListDtosList = blackListList.stream().map(b -> new BlackListDto(b.getId(), b.getEmail(), b.isBlack_check())).collect(Collectors.toList());

        boolean hasNext = isHasNext(pageable,collectBlackListDtosList);

        return  new SliceImpl<>(collectBlackListDtosList,pageable,hasNext);

    }

    @Override
    public Slice<BlackListDto> searchEmailBlackList(String email, Long lastId,Pageable pageable){
        BooleanBuilder builder = getBuilder(lastId);
        if(email!=null){
            builder.and(blackList.email.like("%" + email + "%"));
        }

        List<BlackList> blackListList = jpaQueryFactory.select(blackList)
                .from(blackList)
                .where(builder)
                .limit(pageable.getPageSize()+1)
                .orderBy(blackList.id.desc())
                .fetch();

        List<BlackListDto> blackListDtoList = blackListList.stream().map(b -> new BlackListDto(b.getId(), b.getEmail(), b.isBlack_check())).collect(Collectors.toList());
        boolean hasNext = isHasNext(pageable, blackListDtoList);
        return  new SliceImpl<>(blackListDtoList,pageable,hasNext);

    }
    private static <T> boolean isHasNext(Pageable pageable, List<T> collect) {
        boolean hasNext =false;
        if (collect.size() > pageable.getPageSize()){
            collect.remove(pageable.getPageSize());
            hasNext= true;
        }
        return hasNext;
    }

    private static BooleanBuilder getBuilder(Long lastId) {
        BooleanBuilder builder = new BooleanBuilder();
        if(lastId != null){
            builder.and(blackList.id.lt(lastId));
        }
        return builder;
    }
}
