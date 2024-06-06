package com.team.RecipeRadar.domain.questions.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomAnswerRepositoryImpl implements CustomAnswerRepository{

    private final JPAQueryFactory  jpaQueryFactory;
}
