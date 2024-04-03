package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.UpdatePasswordDto;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;

import java.util.List;
import java.util.Map;

public interface AccountRetrievalService {

    List<Map<String ,String>> findLoginId(String username, String email, String code);

    Map<String,Object> findPwd(String username, String loginId, String email,String code);

    ControllerApiResponse updatePassword(UpdatePasswordDto memberDto, String uuid);
}
