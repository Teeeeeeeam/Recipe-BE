package com.team.RecipeRadar.domain.member.dao;

import com.team.RecipeRadar.domain.member.domain.AccountRetrieval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRetrievalRepository extends JpaRepository<AccountRetrieval, String> {

    void deleteByVerificationId(String verificationId);

    AccountRetrieval findByLoginIdAndVerificationId(String loginId,String verificationId);

    boolean existsByLoginIdAndVerificationId(String loginId,String  verificationId);
}
