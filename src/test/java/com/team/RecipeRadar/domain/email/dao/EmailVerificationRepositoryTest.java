package com.team.RecipeRadar.domain.email.dao;

import com.team.RecipeRadar.domain.email.domain.EmailVerification;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class EmailVerificationRepositoryTest {
    
    @Autowired EmailVerificationRepository emailVerificationRepository;
    @Autowired EntityManager entityManager;

    private EmailVerification emailVerification;

    @BeforeEach
    void setUp(){
        emailVerification = EmailVerification.builder().email("test@email.com").code(123456).build();
        emailVerificationRepository.save(emailVerification);
    }
    
    @Test
    @DisplayName("이메일과 인증코드가 일치하는 이메일 검증 객체 조회 - 성공")
    void findByEmailAndCodeSuccess(){
        EmailVerification byEmailAndCode = emailVerificationRepository.findByEmailAndCode("test@email.com", 123456);
        assertThat(byEmailAndCode).isNotNull();
    }


    @Test
    @DisplayName("이메일과 인증코드가 일치하는 이메일 검증 객체 조회 - 실패")
    void findByEmailAndCodeFail(){
        EmailVerification byEmailAndCode = emailVerificationRepository.findByEmailAndCode("no@email.com", 123456);
        assertThat(byEmailAndCode).isNull();
    }

    @Test
    @DisplayName("이메일과 인증번호가 일치하는 객체 삭제")
    void deleteEmailAndCode(){
        emailVerificationRepository.deleteByEmailAndCode("test@email.com",123456);
        entityManager.flush();

        List<EmailVerification> all = emailVerificationRepository.findAll();
        assertThat(all).isEmpty();
    }

}