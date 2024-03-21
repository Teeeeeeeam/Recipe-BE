package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.AddPostRequest;
import com.team.RecipeRadar.domain.post.dto.UpdatePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Post save(AddPostRequest request) {
        return postRepository.save(request.toEntity());
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    @Override
    public void delete(long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Post update(long id, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        post.update(request.getPostTitle(), request.getPostContent(), request.getPostServing(), request.getPostCookingTime(), request.getPostCookingLevel());

        return post;
    }
}
