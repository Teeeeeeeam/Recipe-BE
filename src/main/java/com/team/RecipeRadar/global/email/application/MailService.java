package com.team.RecipeRadar.global.email.application;


import java.util.Map;

public interface MailService {

    String sensMailMessage(String email);

    String createCode();

    String getCode();

    Map<String, Boolean> verifyCode(String code);

}
