package com.team.RecipeRadar.domain.post.dao;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.user.PostDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.comment.domain.QComment.*;
import static com.team.RecipeRadar.domain.member.domain.QMember.*;
import static com.team.RecipeRadar.domain.post.domain.QPost.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

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

        List<Tuple> list = jpaQueryFactory.select(post.id, post.postTitle, post.postImageUrl, post.member.nickName)
                .from(post)
                .orderBy(post.created_at.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNextSize = false;

        List<PostDto> collect = list.stream().map(tuple -> new PostDto(tuple.get(post.id), tuple.get(post.postTitle), tuple.get(post.postImageUrl), tuple.get(post.member.nickName))).collect(Collectors.toList());


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

        List<Tuple> list = jpaQueryFactory.select(post,comment)
                .from(post)
                .join(comment).on(comment.post.id.eq(post.id))
                .where(post.id.eq(postId)).fetch();

        if (list.isEmpty()) {
            throw new NoSuchElementException("해당하는 게시물이 없습니다.");
        }
        
        PostDto postDto = list.stream().map(tuple -> PostDto.of(tuple.get(post))).findFirst().get();
        List<CommentDto> collect = list.stream().map(tuple -> CommentDto.of(tuple.get(comment))).collect(Collectors.toList());


        return new PostDetailResponse(postDto,collect);
    }
}
