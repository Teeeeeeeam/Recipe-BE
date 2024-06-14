package com.team.RecipeRadar.domain.admin.application.post;

import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.like.dao.PostLikeRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.user.PostResponse;
import com.team.RecipeRadar.global.exception.ex.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminPostServiceImpl implements AdminPostService{

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ImgRepository imgRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    public long searchAllPosts() {
        return postRepository.countAllBy();
    }

    /**
     * 게시글의 작성된 댓글을 조회하는 메서드
     * 게시글의 작성된 모든 데이터를 무한 페이징으로 처리합니다.
     */
    @Override
    public PostsCommentResponse getPostsComments(Long postId, Long lastId, Pageable pageable) {
        Slice<CommentDto> postComment = commentRepository.getPostComment(postId, lastId, pageable);

        return new PostsCommentResponse(postComment.hasNext(),postComment.getContent());
    }


    /**
     * 게시글을 조회하는 메서드
     * dao 넘어온 PostDto의 페이징의 대한 데이터를 PostResponse의 담아서 변환합니다.
     */
    @Override
    public PostResponse searchPost(String loginId, String recipeTitle, String postTitle, Long lastPostId, Pageable pageable) {
        Slice<PostDto> postDtoList = postRepository.searchPosts(loginId, recipeTitle, postTitle, lastPostId, pageable);
        return new PostResponse(postDtoList.hasNext(),postDtoList.getContent());
    }

    /**
     * 게시글의 댓글 삭제하는 메서드
     * 어드민 사용자는 게시글의 작성된 댓글을 삭제 가능합니다.
     */
    @Override
    public void deleteComments(List<Long> commentsIds) {
        commentsIds.forEach(id -> {
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_COMMENT));
            commentRepository.deleteById(comment.getId());
        });
    }

    /**
     * 게스글을 삭제하는 메서드
     * 한개 이상의 게시글을 삭제할수 있습니다.
     */
    @Override
    public void deletePosts(List<Long> postIds) {
        postIds.forEach(postId -> {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_POST));

            imgRepository.deletePostImg(post.getId(), post.getRecipe().getId());
            commentRepository.deletePostID(post.getId());
            postLikeRepository.deletePostID(postId);
            postRepository.deleteById(post.getId());
        });
    }
}
