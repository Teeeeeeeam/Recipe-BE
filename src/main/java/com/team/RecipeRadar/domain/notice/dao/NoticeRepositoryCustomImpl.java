package com.team.RecipeRadar.domain.notice.dao;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.notice.domain.QNotice;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.global.Image.domain.QUploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.notice.domain.QNotice.*;
import static com.team.RecipeRadar.global.Image.domain.QUploadFile.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Value("${S3.URL}")
    private  String s3URL;

    public List<NoticeDto> mainNotice(){
        List<Tuple> list = jpaQueryFactory.select(notice.id, notice.noticeTitle, uploadFile.storeFileName)
                .from(notice)
                .join(uploadFile).on(uploadFile.notice.id.eq(notice.id))
                .orderBy(notice.created_at.desc())
                .limit(5).fetch();

        return list.stream().map(tuple -> NoticeDto.of(tuple.get(notice.id), tuple.get(notice.noticeTitle), getImageUrl(tuple)))
                .collect(Collectors.toList());
    }

    private  String getImageUrl(Tuple tuple) {
        String img = tuple.get(uploadFile.storeFileName);
        if(!img.startsWith("http")){
            return s3URL+img;
        }
        return img;
    }
}

