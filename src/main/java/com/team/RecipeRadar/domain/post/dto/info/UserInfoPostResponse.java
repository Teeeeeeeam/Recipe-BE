package com.team.RecipeRadar.domain.post.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoPostResponse {

    private boolean nextPage;

    private List<UserInfoPostRequest> content;
}
