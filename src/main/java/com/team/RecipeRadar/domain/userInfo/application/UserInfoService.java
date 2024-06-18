package com.team.RecipeRadar.domain.userInfo.application;

import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoBookmarkResponse;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;

public interface UserInfoService {

    UserInfoResponse getMembers(Long memberId);

    void updateNickName(String nickName,Long memberId);

    void updateEmail(String email,Integer code,Long memberId);

    String userToken(String loginId,String authenticationName, String password,String loginType);
    void deleteMember(String loginId, boolean checkType ,String authenticationName);

    boolean validUserToken(String encodeToken,String loginId);

    UserInfoBookmarkResponse userInfoBookmark(Long memberId, Long lastId, Pageable pageable);
}
