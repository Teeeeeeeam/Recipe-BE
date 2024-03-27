package com.team.RecipeRadar.domain.member.application;

import java.util.List;
import java.util.Map;

public interface AccountRetrievalService {

    List<Map<String ,String>> findLoginId(String username, String email, String code);


}
