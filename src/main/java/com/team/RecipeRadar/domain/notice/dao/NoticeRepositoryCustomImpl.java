package com.team.RecipeRadar.domain.notice.dao;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.notice.domain.QNotice;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.global.Image.domain.QUploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

    public Slice<NoticeDto> adminNotice(Pageable pageable){
        List<Tuple> list = jpaQueryFactory.select(notice, notice.member.nickName)
                .from(notice)
                .orderBy(notice.created_at.desc())      //최신순 정렬
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<NoticeDto> noticeDtoList = list.stream().map(tuple -> NoticeDto.of(tuple.get(notice), tuple.get(notice.member.nickName))).collect(Collectors.toList());


        boolean isHasNext = false;

        if(noticeDtoList.size()> pageable.getPageSize()){
            noticeDtoList.remove(pageable.getPageSize());
            isHasNext = true;
        }

        return new SliceImpl<>(noticeDtoList,pageable,isHasNext);
    }
    private  String getImageUrl(Tuple tuple) {
        String img = tuple.get(uploadFile.storeFileName);
        if(!img.startsWith("http")){
            return s3URL+img;
        }
        return img;
    }

    private static boolean isHasNext(Pageable pageable, List<RecipeDto> content) {
        boolean hasNext =false;

        if (content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return hasNext;
    }
}

