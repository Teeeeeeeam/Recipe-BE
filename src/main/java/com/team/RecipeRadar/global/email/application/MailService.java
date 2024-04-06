package com.team.RecipeRadar.global.email.application;


import java.util.Map;

public interface MailService {

    String sensMailMessage(String email);

    Integer createCode();

    Integer getCode(String email,int code);

    Map<String, Boolean> verifyCode(String email, int code);

    void deleteCode(String email,int code);

}
