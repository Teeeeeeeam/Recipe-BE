package com.team.RecipeRadar.repository;

import com.team.RecipeRadar.Entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification,String> {

    EmailVerification findByUsername(String username);

    boolean existsByUsername(String username);   //사용자가 있는지 확인
}
