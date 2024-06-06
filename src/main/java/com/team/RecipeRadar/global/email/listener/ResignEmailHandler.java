package com.team.RecipeRadar.global.email.listener;

import com.team.RecipeRadar.global.email.application.MailService;
import com.team.RecipeRadar.global.email.event.ResignMemberEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResignEmailHandler{


    @Qualifier("ResignEmail")
    private final MailService mailService;

    @EventListener
    public void sendEmail(ResignMemberEvent event) {
        String eventEmail = event.getEmail();
        mailService.sensMailMessage(eventEmail);
    }
}
