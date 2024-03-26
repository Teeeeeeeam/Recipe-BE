package com.team.RecipeRadar.global.security.oauth2.provider;

import java.util.Map;

public class NaverUserInfo implements Oauth2UserInfo{

    private Map<String,Object> attribute;
    private Map<String,String> naverAttribute;

    public NaverUserInfo(Map<String,Object> attribute) {
        this.attribute = attribute;
        this.naverAttribute= (Map<String, String>) this.attribute.get("response");
    }

    @Override
    public String getId() {
        return naverAttribute.get("id");
    }

    @Override
    public String getEmail() {
        return naverAttribute.get("email");
    }

    @Override
    public String getName() {
        return naverAttribute.get("name");
    }
}
