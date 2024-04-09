package com.team.RecipeRadar.domain.like.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserInfoPostLikeResponse {

    private boolean nextPage;

    private List<UserLikeDto> content;


}
