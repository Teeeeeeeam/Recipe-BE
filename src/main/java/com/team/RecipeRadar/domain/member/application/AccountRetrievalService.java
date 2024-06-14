package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.UpdatePasswordRequest;

import java.util.List;
import java.util.Map;

public interface AccountRetrievalService {

    List<Map<String ,String>> findLoginId(String username, String email, int code);

    String findPwd(String username, String loginId, String email,int code);

    void updatePassword(UpdatePasswordRequest updatePasswordRequest, String uuid);
}
