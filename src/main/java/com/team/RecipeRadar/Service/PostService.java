package com.team.RecipeRadar.Service;

import com.team.RecipeRadar.Entity.Post;
import com.team.RecipeRadar.dto.AddPostRequest;
import com.team.RecipeRadar.dto.UpdatePostRequest;

import java.util.List;

public interface PostService {
    Post save(AddPostRequest request);

    List<Post> findAll();

    Post findById(long id);

    void delete(long id);

    Post update(long id, UpdatePostRequest request);
}
