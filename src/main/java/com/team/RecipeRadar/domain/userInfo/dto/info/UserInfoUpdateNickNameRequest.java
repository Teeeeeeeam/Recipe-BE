package com.team.RecipeRadar.domain.userInfo.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "사용자 닉네임 변경 Request")
public class UserInfoUpdateNickNameRequest {

    @Schema(example = "변경 닉네임")
    private String nickName;
}
