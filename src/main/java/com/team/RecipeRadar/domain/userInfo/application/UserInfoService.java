package com.team.RecipeRadar.domain.userInfo.application;

import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;

public interface UserInfoService {

    UserInfoResponse getMembers(String loginId, String authName);

    void updateNickName(String nickName,String loginId,String authName);

    void updateEmail(String email,String code,String loginId,String authName);
}
