package com.team.RecipeRadar.domain.visit.dao;


import com.team.RecipeRadar.domain.visit.domain.VisitStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitStatisticsRepository extends JpaRepository<VisitStatistics,Long>, CustomVisitAdminRepository {

    @Query("SELECT SUM(v.visitCount) AS count FROM VisitStatistics v")
    int getAllCount();

}
