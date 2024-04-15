package com.team.RecipeRadar.domain.userInfo.application;

import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;
import org.springframework.security.core.Authentication;

public interface UserInfoService {

    UserInfoResponse getMembers(String loginId, String authName);

    void updateNickName(String nickName,String loginId,String authName);

    void updateEmail(String email,String code,String loginId,String authName);

    String userToken(String loginId,String authenticationName, String password);
}
