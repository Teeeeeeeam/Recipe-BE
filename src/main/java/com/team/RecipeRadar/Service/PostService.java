package com.team.RecipeRadar.service;

import com.team.RecipeRadar.Entity.Post;
import com.team.RecipeRadar.dto.AddPostRequest;
import com.team.RecipeRadar.dto.UpdatePostRequest;
import com.team.RecipeRadar.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public Post save(AddPostRequest request) {
        return postRepository.save(request.toEntity());
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public Post update(long id, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        post.update(request.getPostTitle(), request.getPostContent(), request.getPostServing(), request.getPostCookingTime(), request.getPostCookingLevel());

        return post;
    }
}
