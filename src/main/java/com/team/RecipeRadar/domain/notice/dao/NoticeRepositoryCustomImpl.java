package com.team.RecipeRadar.domain.notice.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.member.domain.QMember.*;
import static com.team.RecipeRadar.domain.notice.domain.QNotice.*;
import static com.team.RecipeRadar.domain.Image.domain.QUploadFile.*;

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
                .leftJoin(uploadFile).on(uploadFile.notice.id.eq(notice.id))
                .orderBy(notice.createdAt.desc())
                .limit(5).fetch();

        return list.stream()
                .map(tuple -> NoticeDto.of(tuple.get(notice.id), tuple.get(notice.noticeTitle), getImageUrl(tuple)))
                .collect(Collectors.toList());
    }

    public Slice<NoticeDto> adminNotice(Long noticeId,Pageable pageable){
        BooleanBuilder builder = new BooleanBuilder();

        lastId(noticeId, builder);

        List<Tuple> list = jpaQueryFactory.select(notice, notice.member.nickName)
                .from(notice)
                .where(builder)
                .orderBy(notice.id.desc())      //최신순 정렬
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<NoticeDto> noticeDtoList = list.stream().map(tuple -> NoticeDto.of(tuple.get(notice), tuple.get(notice.member.nickName))).collect(Collectors.toList());


        boolean isHasNext = isNext(pageable, noticeDtoList);

        return new SliceImpl<>(noticeDtoList,pageable,isHasNext);
    }

    @Override
    public NoticeDto detailsPage(Long noticeId) {

        List<Tuple> list = jpaQueryFactory.select(uploadFile.storeFileName,notice)
                .from(notice)
                .leftJoin(uploadFile).on(uploadFile.notice.id.eq(notice.id))
                .where(notice.id.eq(noticeId))
                .fetch();

        return list.stream()
                .map(tuple -> NoticeDto.detailsOf(tuple.get(notice), getImageUrl(tuple))).findFirst()
                .orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_NOTICE));
    }

    @Override
    public void deleteMemberId(Long memberId) {
        jpaQueryFactory.delete(notice)
                .where(notice.member.id.in(
                        JPAExpressions.select(member.id)
                                .from(member).where(member.id.eq(memberId))
                )).execute();
    }

    @Override
    public Slice<NoticeDto> searchNotice(String title, Long lastId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if(title != null){
            builder.and(notice.noticeTitle.like("%"+title+"%"));
        }
        lastId(lastId, builder);

        List<Notice> notices = jpaQueryFactory.select(notice)
               .from(notice)
                .where(builder)
                .orderBy(notice.id.desc())
                .limit(pageable.getPageSize() + 1).fetch();

        List<NoticeDto> noticeDtoList = notices.stream().map(notice -> NoticeDto.of(notice, notice.getMember().getNickName())).collect(Collectors.toList());
        boolean nextPage = isNext(pageable, noticeDtoList);

        return new SliceImpl(noticeDtoList,pageable,nextPage);
    }

    private static void lastId(Long lastId, BooleanBuilder builder) {
        if(lastId !=null){
            builder.and(notice.id.lt(lastId));
        }
    }

    private  String getImageUrl(Tuple tuple) {
        String img = tuple.get(uploadFile.storeFileName);
        if(img!=null && !img.startsWith("http")){
            return s3URL+img;
        }
        return img;
    }

    private static<T> boolean isNext(Pageable pageable, List<T> noticeDtoList) {
        boolean isHasNext = false;

        if(noticeDtoList.size()> pageable.getPageSize()){
            noticeDtoList.remove(pageable.getPageSize());
            isHasNext = true;
        }
        return isHasNext;
    }
}

