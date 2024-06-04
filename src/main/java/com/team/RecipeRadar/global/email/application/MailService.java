package com.team.RecipeRadar.global.email.application;


import java.util.Map;

public interface MailService {

    String sensMailMessage(String email);

    default Integer createCode() {
        return null;
    }

    default Integer getCode(String email, int code) {
        return null;
    }

    default Map<String, Boolean> verifyCode(String email, int code) {
        return null;
    }

    default void deleteCode(String email, int code) {
    }

}
