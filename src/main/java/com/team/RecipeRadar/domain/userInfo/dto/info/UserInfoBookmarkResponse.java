package com.team.RecipeRadar.domain.userInfo.dto.info;

import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoBookmarkResponse {

    private Boolean nextPage;
    private List<RecipeDto> bookmarkList;

}
