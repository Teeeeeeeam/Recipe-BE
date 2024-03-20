package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Post;
import com.team.RecipeRadar.Entity.Recipe;
import lombok.Getter;

@Getter
public class PostResponse {

    private final String postTitle;
    private final String postContent;
    private final String postServing;
    private final String postCookingTime;
    private final String postCookingLevel;


    public  PostResponse(Post post) {
        this.postTitle = post.getPostTitle();
        this.postContent = post.getPostContent();
        this.postServing = post.getPostServing();
        this.postCookingTime = post.getPostCookingTime();
        this.postCookingLevel = post.getPostCookingLevel();
    }
}
