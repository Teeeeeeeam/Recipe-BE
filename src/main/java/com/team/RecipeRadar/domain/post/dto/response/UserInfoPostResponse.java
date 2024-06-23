package com.team.RecipeRadar.domain.post.dto.response;

import com.team.RecipeRadar.domain.post.dto.request.UserInfoPostRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoPostResponse {

    private boolean nextPage;

    private List<UserInfoPostRequest> content;
}
