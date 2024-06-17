package com.team.RecipeRadar.domain.visit.dao;

import com.team.RecipeRadar.domain.visit.domain.VisitSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitSessionRepository extends JpaRepository<VisitSession,Long> {

    @Query("SELECT count(v) FROM VisitSession v WHERE DATE(v.expiredAt) = CURRENT_DATE")
    Integer getCurrentCount();
    boolean existsByIpAddress(String uuid);
}
