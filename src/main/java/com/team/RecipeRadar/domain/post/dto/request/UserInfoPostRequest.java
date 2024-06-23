package com.team.RecipeRadar.domain.post.dto.request;

import com.team.RecipeRadar.domain.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoPostRequest {

    private Long id;

    private String postTitle;


    public static UserInfoPostRequest of(Post post) {
        return new UserInfoPostRequest(post.getId(), post.getPostTitle());
    }
}
