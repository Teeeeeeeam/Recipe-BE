package com.team.RecipeRadar.global.visit.dao;

import com.team.RecipeRadar.global.visit.domain.VisitCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<VisitCount,Long> {

    @Query("SELECT count(v) FROM VisitCount v WHERE DATE(v.expired_at) = CURRENT_DATE")
    Integer getCurrentCount();
    boolean existsByIpAddress(String uuid);
}
