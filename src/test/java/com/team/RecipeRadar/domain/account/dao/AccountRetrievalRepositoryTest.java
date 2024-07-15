package com.team.RecipeRadar.domain.account.dao;

import com.team.RecipeRadar.domain.account.domain.AccountRetrieval;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@Import(QueryDslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class AccountRetrievalRepositoryTest {

    @Autowired AccountRetrievalRepository accountRetrievalRepository;
    @Autowired EntityManager entityManager;

    private AccountRetrieval accountRetrieval;

    @BeforeEach
    void setUp(){
        accountRetrieval = AccountRetrieval.builder().loginId("loginId").expireAt(LocalDateTime.now().plusMinutes(3)).build();
        accountRetrievalRepository.save(accountRetrieval);
    }
    
    @Test
    @DisplayName("인증 정보 삭제")
    void deleteByVerificationId(){
        accountRetrievalRepository.deleteByVerificationId(accountRetrieval.getVerificationId());
        entityManager.flush();

        List<AccountRetrieval> all = accountRetrievalRepository.findAll();
        assertThat(all).isEmpty();

    }
    
    @Test
    @DisplayName("아이디와 UUID를 이용한 조회")
    void findByLoginIdAndVerificationId(){
        AccountRetrieval byLoginIdAndVerificationId = accountRetrievalRepository.findByLoginIdAndVerificationId(accountRetrieval.getLoginId(), accountRetrieval.getVerificationId());

        assertThat(byLoginIdAndVerificationId.getLoginId()).isEqualTo("loginId");
    }
}