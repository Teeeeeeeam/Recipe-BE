package com.team.RecipeRadar.domain.account.application;

import com.team.RecipeRadar.domain.account.dto.request.UpdatePasswordRequest;

import java.util.List;
import java.util.Map;

public interface AccountRetrievalService {

    List<Map<String ,String>> findLoginId(String username, String email, int code);

    String findPwd(String username, String loginId, String email,int code);

    void updatePassword(UpdatePasswordRequest updatePasswordRequest, String uuid,String cookieType);
}
