package com.team.RecipeRadar.domain.email.event;

import com.team.RecipeRadar.domain.email.application.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResignEmailHandler{


    @Qualifier("ResignEmail")
    private final MailService mailService;

    @Qualifier("QuestionEmail")
    private final MailService questionEmailService;

    @Qualifier("NoneQuestionEmail")
    private final MailService noneEmailService;

    @EventListener
    public void sendEmail(MailEvent event) {
        String eventEmail = event.getEmail();
        mailService.sensMailMessage(eventEmail);
    }

    @EventListener
    public void sendEmail(QuestionMailEvent event) {
        String eventEmail = event.getEmail();
        questionEmailService.sensMailMessage(eventEmail);
    }

    @EventListener
    public void sendEmail(NoneQuestionMailEvent event) {
        String eventEmail = event.getEmail();
        String subject = event.getSubject();
        String body = event.getBody();
        noneEmailService.sendMail(eventEmail,subject,body);
    }
}
