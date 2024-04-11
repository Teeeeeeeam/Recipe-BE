package com.team.RecipeRadar.domain.userInfo.dto.info;

import lombok.Data;

@Data
public class UserInfoUpdateNickNameRequest {

    private String loginId;

    private String nickName;
}
