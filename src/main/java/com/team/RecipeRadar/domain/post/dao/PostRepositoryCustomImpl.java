package com.team.RecipeRadar.domain.post.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.domain.QComment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.request.UserInfoPostRequest;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.comment.domain.QComment.*;
import static com.team.RecipeRadar.domain.member.domain.QMember.*;
import static com.team.RecipeRadar.domain.post.domain.QPost.*;
import static com.team.RecipeRadar.domain.Image.domain.QUploadFile.*;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Value("${S3.URL}")
    private String S3URL;

    private final String FULL_TEXT_INDEX ="function('match',{0},{1})";

    @Override
    public Slice<UserInfoPostRequest> userInfoPost(Long memberId,Long lastId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if (lastId!=null){
            builder.and(post.id.gt(lastId));
        }
        List<Post> postList = jpaQueryFactory.selectFrom(post)
                .innerJoin(post.member, member).fetchJoin()
                .where(builder,post.member.id.eq(memberId))
                .orderBy(post.member.id.desc())
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
    public Slice<PostDto> getAllPost(Long postId,Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        if(postId!=null){
            builder.and(post.id.lt(postId));
        }
        List<Tuple> list = jpaQueryFactory.select(post.id, post.member.loginId,post.postTitle, uploadFile.storeFileName, post.member.nickName, post.recipe.title,post.recipe.id,post.createdAt)
                .from(post)
                .join(uploadFile).on(post.id.eq(uploadFile.post.id))
                .where(builder.and(uploadFile.notice.isNull().and(uploadFile.post.id.isNotNull())))
                .orderBy(uploadFile.post.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<PostDto> postDtoList = list.stream().map(tuple -> PostDto.of(tuple.get(post.id),tuple.get(post.member.loginId), tuple.get(post.postTitle),
                getImg(tuple), tuple.get(post.member.nickName),tuple.get(post.recipe.title),tuple.get(post.recipe.id),tuple.get(post.createdAt))).collect(Collectors.toList());

        boolean hasNextSize = isHasNextSize(pageable, postDtoList);

        return new SliceImpl(postDtoList,pageable,hasNextSize);
    }

    /**
     * 게시글의 상세 정보를 위해 게시글의 id를 통해서 해당 게시글의 포함된 댓글까지도 모두 조회
     */
    @Override
    public PostDto postDetails(Long postId) {

        List<Tuple> list = jpaQueryFactory.select(post,uploadFile.storeFileName,comment, post.recipe)
                .from(post)
                .leftJoin(comment).on(comment.post.id.eq(post.id))
                .join(uploadFile).on(post.recipe.id.eq(uploadFile.recipe.id).and(post.id.eq(uploadFile.post.id)))
                .where(post.id.eq(postId)).fetch();

        if (list.isEmpty()) {
            throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_POST);
        }

        List<CommentDto> comments = list.stream().map(
                tuple -> {
                    Comment comment = tuple.get(QComment.comment);
                    if (comment != null) {
                        return CommentDto.of(comment);
                    } else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return list.stream().map(tuple -> PostDto.of(tuple.get(post),getImg(tuple),tuple.get(post.recipe),comments)).findFirst().get();
    }

    /**
     * 사용자의 게시글을 검색하는 쿼리 loginId,recipeTitle,postTitle,lastPostId 의 대해서 단일 조건으로 검색을 하고 모든 하나씩 추가될 때마다 and 조건으로 검색데이터수를 줄여감
     * @param loginId   작성한 사용자 로그인 아이디
     * @param recipeTitle   스크랩한 레시피 제목
     * @param postTitle     작성한 레시피 제목
     * @param lastPostId    마지막 포스트아이디 (무한페이징 사용)
     * @param pageable  
     * @return
     */
    @Override
    public Slice<PostDto> searchPosts(String loginId, String recipeTitle, String postTitle ,Long lastPostId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if(lastPostId !=null){
            builder.and(post.id.lt(lastPostId));
        }
        if(recipeTitle!=null){
            NumberTemplate recipe_boolean = Expressions.numberTemplate(Double.class,
                    FULL_TEXT_INDEX , post.recipe.title, recipeTitle);
            builder.and(recipe_boolean.gt(0));
        }
        if(postTitle!=null){
            NumberTemplate post_boolean = Expressions.numberTemplate(Double.class,
                    FULL_TEXT_INDEX , post.postTitle, postTitle);
            builder.and(post_boolean.gt(0));
        }
        if (loginId != null) {
            builder.and(post.member.loginId.eq(loginId));
        }

        List<Tuple> list = jpaQueryFactory
                .select(post.id, post.member.loginId, post.postTitle, uploadFile.storeFileName,
                        post.member.nickName, post.recipe.title, post.recipe.id, post.createdAt)
                .from(post)
                .join(uploadFile).on(post.id.eq(uploadFile.post.id).and(post.recipe.id.eq(uploadFile.recipe.id)))
                .where(builder)
                .orderBy(post.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<PostDto> postDtoList = list.stream().map(tuple -> PostDto.of(tuple.get(post.id),tuple.get(post.member.loginId), tuple.get(post.postTitle),
                getImg(tuple), tuple.get(post.member.nickName),tuple.get(post.recipe.title),tuple.get(post.recipe.id),tuple.get(post.createdAt))).collect(Collectors.toList());

        boolean hasNextSize = isHasNextSize(pageable, postDtoList);

        return new SliceImpl(postDtoList,pageable,hasNextSize);
    }
    @Override
    public void deletePostByRecipeId(Long recipeId) {
        jpaQueryFactory.delete(post)
                .where(post.recipe.id.in(recipeId)).execute();
    }

    @Override
    public List<PostDto> getTopRecipesByLikes(Long recipeId) {
        List<Tuple> list = jpaQueryFactory
                .select(post,uploadFile.storeFileName)
                .from(post)
                .join(uploadFile).on(uploadFile.post.id.eq(post.id))
                .where(post.recipe.id.in(recipeId))
                .orderBy(post.postLikeCount.desc())
                .limit(4)
                .fetch();

        return list.stream().map(tuple -> PostDto.of(tuple.get(post), getImg(tuple))).collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getTopMainByLikes() {
        List<Tuple> list = jpaQueryFactory
                .select(post, uploadFile.storeFileName)
                .from(post)
                .join(uploadFile).on(uploadFile.post.id.eq(post.id))
                .orderBy(post.postLikeCount.desc())
                .limit(3)
                .fetch();
        return list.stream().map(tuple -> PostDto.of(tuple.get(post), getImg(tuple))).collect(Collectors.toList());
    }

    @Override
    public Slice<PostDto> getPostsByRecipeId(Long recipeId, Integer lastCount,Long lastId,Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        if(lastCount!=null){
            if(lastCount==0){
                builder.and(post.postLikeCount.eq(0).and(post.id.lt(lastId)));
            }else
                builder.and(post.postLikeCount.lt(lastCount));
        }

        OrderSpecifier<Integer> orderByLikeCount = new CaseBuilder()
                .when(post.postLikeCount.gt(0)).then(post.postLikeCount)
                .otherwise(Expressions.constant(0))
                .desc();

        OrderSpecifier<Long> orderByPostId = new CaseBuilder()
                .when(post.postLikeCount.eq(0)).then(post.id)
                .otherwise(Expressions.constant(Long.MAX_VALUE))
                .desc();

        List<Tuple> list = jpaQueryFactory.select(post, uploadFile.storeFileName)
                .from(post)
                .join(uploadFile).on(uploadFile.post.id.eq(post.id))
                .where(builder,post.recipe.id.eq(recipeId))
                .orderBy(orderByLikeCount,orderByPostId)
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<PostDto> postDtoList = list.stream().map(tuple -> PostDto.of(tuple.get(post), getImg(tuple))).collect(Collectors.toList());
        boolean nextSize = isHasNextSize(pageable, postDtoList);

        return new SliceImpl<>(postDtoList,pageable,nextSize);
    }

    private String getImg(Tuple tuple) {
        return S3URL+tuple.get(uploadFile.storeFileName);
    }

    private static boolean isHasNextSize(Pageable pageable, List<PostDto> collect) {
        boolean hasNextSize = false;
        if(collect.size()> pageable.getPageSize()){
            collect.remove(pageable.getPageSize());
            hasNextSize = true;
        }
        return hasNextSize;
    }
}
