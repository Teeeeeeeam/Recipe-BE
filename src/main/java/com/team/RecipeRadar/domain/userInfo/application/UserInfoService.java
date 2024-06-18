package com.team.RecipeRadar.domain.userInfo.application;

import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoBookmarkResponse;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;
import org.springframework.data.domain.Pageable;
public interface UserInfoService {

    UserInfoResponse getMembers(Long memberId);

    void updateNickName(String nickName,Long memberId);

    void updateEmail(String email,Integer code,Long memberId);

    String userToken(Long loginId, String password);

    void deleteMember(Long memberId, boolean checkType);

    UserInfoBookmarkResponse userInfoBookmark(Long memberId, Long lastId, Pageable pageable);
}
