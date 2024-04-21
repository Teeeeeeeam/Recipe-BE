package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.UpdatePasswordRequest;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;

import java.util.List;
import java.util.Map;

public interface AccountRetrievalService {

    List<Map<String ,String>> findLoginId(String username, String email, int code);

    Map<String,Object> findPwd(String username, String loginId, String email,int code);

    ControllerApiResponse updatePassword(UpdatePasswordRequest updatePasswordRequest, String uuid);
}
