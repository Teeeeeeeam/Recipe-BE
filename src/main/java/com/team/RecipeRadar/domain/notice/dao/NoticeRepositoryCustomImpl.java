package com.team.RecipeRadar.domain.notice.dao;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.info.AdminInfoNoticeRequest;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.member.domain.QMember.member;
import static com.team.RecipeRadar.domain.notice.domain.QNotice.notice;
import static com.team.RecipeRadar.global.Image.domain.QUploadFile.uploadFile;


@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


}

