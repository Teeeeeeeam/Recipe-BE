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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

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

    @Override
    public PostsCommentResponse getPostsComments(Long postId, Long lastId, Pageable pageable) {
        Slice<CommentDto> postComment = commentRepository.getPostComment(postId, lastId, pageable);

        return new PostsCommentResponse(postComment.hasNext(),postComment.getContent());
    }


    /**
     * dao 넘어온 PostDto의 페이징의 대한 데이터를 PostResponse의 담아서 변환
     */
    @Override
    public PostResponse searchPost(String loginId, String recipeTitle, String postTitle, Long lastPostId, Pageable pageable) {
        Slice<PostDto> postDtos = postRepository.searchPosts(loginId, recipeTitle, postTitle, lastPostId, pageable);
        return new PostResponse(postDtos.hasNext(),postDtos.getContent());
    }

    /**
     * 어디민 사용자는 댓글을 단일,일괄 삭제
     * @param ids
     */
    @Override
    public void deleteComments(List<Long> ids) {
        for (Long id : ids) {
            Comment comment = commentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("댓글을 찾을수 없습니다."));
            commentRepository.deleteById(comment.getId());
        }
    }

    /**
     * 한개 이상의 게시글을 삭제할수 있다.
     * @param postIds
     */
    @Override
    public void deletePosts(List<Long> postIds) {

        for (Long postId : postIds) {
            Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("해당 게시물을 찾을수 없습니다."));
            imgRepository.deletePostImg(post.getId(),post.getRecipe().getId());
            commentRepository.deletePostID(post.getId());
            postLikeRepository.deletePostID(postId);
            postRepository.deleteById(post.getId());
        }
    }
}
