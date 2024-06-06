package com.team.RecipeRadar.global.email.application;


import java.util.Map;

public interface MailService {

    default String sensMailMessage(String email) {
        return null;
    }
    default String sendMail(String email, String subject, String body) {
        return null;
    }

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
