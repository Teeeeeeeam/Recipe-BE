package com.team.RecipeRadar.domain.like.dto.response;

import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserInfoLikeResponse {

    private boolean nextPage;

    private List<UserLikeDto> content;

}
