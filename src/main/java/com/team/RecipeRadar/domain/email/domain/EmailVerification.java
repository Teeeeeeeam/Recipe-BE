package com.team.RecipeRadar.domain.email.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification{

    @Id
    @GeneratedValue(generator = "UUID_GENERATOR")
    @GenericGenerator(name = "UUID_GENERATOR", strategy = "org.hibernate.id.UUIDGenerator")
    private String verificationId;

    private String username;

    private String email;

    private Integer code;

    private LocalDateTime creatAt;

    private LocalDateTime expiredAt;

    public static EmailVerification creatEmailVerification(LocalDateTime expiredAt,String email,int code){
        return EmailVerification.builder().email(email).creatAt(LocalDateTime.now()).email(email).code(code).expiredAt(expiredAt).build();
    }
    public boolean expired(EmailVerification emailVerification){
        return  emailVerification.getExpiredAt().isAfter(LocalDateTime.now());
    }

}