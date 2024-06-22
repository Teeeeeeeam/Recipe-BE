package com.team.RecipeRadar.domain.account.dao;

import com.team.RecipeRadar.domain.account.domain.AccountRetrieval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRetrievalRepository extends JpaRepository<AccountRetrieval, String> {

    void deleteByVerificationId(String verificationId);

    AccountRetrieval findByLoginIdAndVerificationId(String loginId,String verificationId);

}
