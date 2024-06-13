package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.domain.BlackListRepository;
import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.like.dao.PostLikeRepository;
import com.team.RecipeRadar.domain.like.dao.RecipeLikeRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.Image.application.ImageService;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminsServiceImpl implements AdminService {

    private final PostRepository postRepository;
    private final RecipeRepository recipeRepository;
    private final PostLikeRepository postLikeRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeBookmarkRepository recipeBookmarkRepository;
    private final CommentRepository commentRepository;
    private final ImageService imageService;
    private final IngredientRepository ingredientRepository;


    @Override
    public long searchAllPosts() {
        return postRepository.countAllBy();
    }

    @Override
    public long searchAllRecipes() {
        return recipeRepository.countAllBy();
    }

    @Override
    public PostsCommentResponse getPostsComments(Long postId, Long lastId, Pageable pageable) {
        Slice<CommentDto> postComment = commentRepository.getPostComment(postId, lastId, pageable);

        return new PostsCommentResponse(postComment.hasNext(),postComment.getContent());
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

    @Override
    public void deleteRecipe(List<Long> ids) {
        for (Long id : ids) {
            deleteRecipeById(id);
        }
    }

    private void deleteRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("해당 레시피를 찾을수 없습니다."));
        Long recipeId = recipe.getId();

        imageService.delete_Recipe(recipeId);
        commentRepository.delete_post(recipeId);
        postLikeRepository.deleteRecipeId(recipeId);
        postRepository.deletePostByRecipeId(recipeId);
        recipeLikeRepository.deleteRecipeId(recipeId);
        recipeBookmarkRepository.deleteAllByRecipe_Id(recipeId);
        ingredientRepository.deleteRecipeId(recipeId);
        recipeRepository.deleteById(recipeId);
    }
}
