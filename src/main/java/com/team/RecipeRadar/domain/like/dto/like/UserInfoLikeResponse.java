package com.team.RecipeRadar.domain.like.dto.like;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserInfoLikeResponse {

    private boolean nextPage;

    private List<UserLikeDto> content;

}
