package com.team.RecipeRadar.global.visit.dao;

import com.team.RecipeRadar.global.visit.domain.VisitCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<VisitCount,Long> {

    boolean existsByIpAddress(String uuid);
}
