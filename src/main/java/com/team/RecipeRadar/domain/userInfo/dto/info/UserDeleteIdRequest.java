package com.team.RecipeRadar.domain.userInfo.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "사용자 탈퇴 Request")
public class UserDeleteIdRequest {

    @Schema(example = "user1234")
    String loginId;

    @Schema(defaultValue ="false")
    boolean checkBox;
}
