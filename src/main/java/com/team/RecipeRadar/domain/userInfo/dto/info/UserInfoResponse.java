package com.team.RecipeRadar.domain.userInfo.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private String username;

    private String nickName;

    private String email;

    private String loginType;

}
