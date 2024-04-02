package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.AddPostRequest;
import com.team.RecipeRadar.domain.post.dto.UpdatePostRequest;
import com.team.RecipeRadar.domain.post.exception.ex.AccessDeniedPostException;
import com.team.RecipeRadar.domain.post.exception.ex.InvalidPostRequestException;
import com.team.RecipeRadar.domain.post.exception.ex.PostNotFoundException;
import com.team.RecipeRadar.domain.post.exception.ex.UnauthorizedPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service

public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Post save(AddPostRequest request) {

        try{
            return postRepository.save(request.toEntity());
        } catch (DataAccessException e) {
            // 데이터베이스 저장 중에 문제가 발생한 경우
            throw new InvalidPostRequestException("요리글 저장에 실패했습니다.", e);
        }
    }
    @Override
    public List<Post> findAll() {
        try {
            return postRepository.findAll();
        } catch (DataAccessException e) {
            // 데이터베이스에서 모든 공지사항을 가져오는 중에 문제가 발생한 경우
            throw new PostNotFoundException("요리글 조회에 실패했습니다.", e);
        }
    }

    @Override
    public Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없습니다." + id));
    }

    @Override
    public void delete(long id) {
        try {
            postRepository.deleteById(id);
        } catch (DataAccessException e) {
            // 데이터베이스에서 공지사항을 삭제하는 중에 문제가 발생한 경우
            throw new AccessDeniedPostException("공지사항 삭제에 실패했습니다." + id, e);
        }

    }

    @Transactional
    @Override
    public Post update(long id, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없습니다." + id));



        try {
            post.update(request.getPostTitle(), request.getPostContent(), request.getPostServing(), request.getPostCookingTime(), request.getPostCookingLevel());
            return post;
        } catch (Exception e) {
            // 업데이트하는 중에 문제가 발생한 경우
            throw new UnauthorizedPostException("요리글 수정에 실패했습니다." + id, e);
        }
    }

}
