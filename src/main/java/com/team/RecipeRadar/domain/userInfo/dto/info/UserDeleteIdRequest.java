package com.team.RecipeRadar.domain.userInfo.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteIdRequest {

    String loginId;

    @Schema(defaultValue ="false")
    boolean checkBox;
}
