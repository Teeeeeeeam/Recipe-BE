package com.team.RecipeRadar.global.security.oauth2.application;

public interface UserDisConnectService {

    String getAccessToken(String auth2Code);

    Boolean disconnect(String accessToken);
}
