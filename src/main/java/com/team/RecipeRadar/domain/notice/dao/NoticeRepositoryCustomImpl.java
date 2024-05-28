package com.team.RecipeRadar.domain.notice.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.notice.domain.QNotice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import static com.team.RecipeRadar.domain.notice.domain.QNotice.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

}

