package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.AddPostRequest;
import com.team.RecipeRadar.domain.post.dto.UpdatePostRequest;

import java.util.List;

public interface PostService {
    Post save(AddPostRequest request);

    List<Post> findAll();

    Post findById(long id);

    void delete(long id);

    Post update(long id, UpdatePostRequest request);
}
