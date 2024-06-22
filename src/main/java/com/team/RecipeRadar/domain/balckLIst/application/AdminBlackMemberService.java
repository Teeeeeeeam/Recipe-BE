package com.team.RecipeRadar.domain.balckLIst.application;

import com.team.RecipeRadar.domain.balckLIst.dto.BlackListResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminBlackMemberService{

    /* 블랙리스트 조회*/
    BlackListResponse getBlackList(Long lastId, Pageable pageable);

    /* 블랙리스트 임시 차단 여부*/
    boolean temporarilyUnblockUser(Long blackId);

    /* 블랙 리스트 삭제*/
    void deleteBlackList(Long blackId);

    /* 블랙 리스트 이메일 조회 */
    BlackListResponse searchEmailBlackList(String email,Long lastId,Pageable pageable);
}
