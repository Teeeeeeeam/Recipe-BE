package com.team.RecipeRadar.domain.post.dao;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.user.PostDetailResponse;
import com.team.RecipeRadar.global.Image.domain.QUploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.comment.domain.QComment.*;
import static com.team.RecipeRadar.domain.member.domain.QMember.*;
import static com.team.RecipeRadar.domain.post.domain.QPost.*;
import static com.team.RecipeRadar.global.Image.domain.QUploadFile.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Value("${S3.URL}")
    private String S3URL;

    @Override
    public Slice<UserInfoPostRequest> userInfoPost(Long memberId, Pageable pageable) {
        List<Post> postList = jpaQueryFactory.selectFrom(post)
                .innerJoin(post.member, member).fetchJoin()
                .where(post.member.id.eq(memberId))
                .orderBy(post.member.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        List<UserInfoPostRequest> infoPostList = postList.stream()
                .map(UserInfoPostRequest::of)
                .collect(Collectors.toList());

        boolean hasNext =false;

        if (infoPostList.size()>pageable.getPageSize()) {
            infoPostList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(infoPostList,pageable,hasNext);
    }

    /**
     * 게시글의 대해서 무한 페이징을 통해 페이징 처리 no-offset 방식을 사용
     */
    @Override
    public Slice<PostDto> getAllPost(Pageable pageable) {

        List<Tuple> list = jpaQueryFactory.select(post.id, post.postTitle, uploadFile.storeFileName, post.member.nickName)
                .from(post)
                .join(uploadFile).on(post.recipe.id.eq(uploadFile.recipe.id).and(post.id.eq(uploadFile.post.id)))
                .orderBy(post.created_at.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNextSize = false;

        List<PostDto> collect = list.stream().map(tuple -> new PostDto(tuple.get(post.id), tuple.get(post.postTitle), getImg(tuple), tuple.get(post.member.nickName))).collect(Collectors.toList());


        if(collect.size()> pageable.getPageSize()){
            collect.remove(pageable.getPageSize());
            hasNextSize = true;
        }
        return new SliceImpl(collect,pageable,hasNextSize);
    }

    /**
     * 게시글의 상세 정보를 위해 게시글의 id를 통해서 해당 게시글의 포함된 댓글까지도 모두 조회
     */
    @Override
    public PostDetailResponse postDetails(Long postId) {

        List<Tuple> list = jpaQueryFactory.select(post,uploadFile.storeFileName,comment)
                .from(post)
                .leftJoin(comment).on(comment.post.id.eq(post.id))
                .join(uploadFile).on(post.recipe.id.eq(uploadFile.recipe.id).and(post.id.eq(uploadFile.post.id)))
                .where(post.id.eq(postId)).fetch();

        if (list.isEmpty()) {
            throw new NoSuchElementException("해당하는 게시물이 없습니다.");
        }

        PostDto postDto = list.stream().map(tuple -> PostDto.of(tuple.get(post),getImg(tuple))).findFirst().get();

        //최초 등록시에는 댓글이 없을수도 있어서 없을때는 빅 베열이 생성
        List<CommentDto> collect1 = list.stream().map(tuple -> {
            Comment comment_entity = tuple.get(comment);
            if (comment_entity != null) {
                return CommentDto.of(comment_entity);
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return new PostDetailResponse(postDto,collect1);
    }

    private String getImg(Tuple tuple) {
        return S3URL+tuple.get(uploadFile.storeFileName);
    }
}
