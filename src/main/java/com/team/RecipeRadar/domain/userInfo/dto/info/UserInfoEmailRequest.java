package com.team.RecipeRadar.domain.userInfo.dto.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoEmailRequest {

    private String email;
    private String code;
    private String loginId;
    private String loginType;

}
