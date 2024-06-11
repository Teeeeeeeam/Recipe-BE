package com.team.RecipeRadar.domain.visit.dao;


import com.team.RecipeRadar.domain.visit.domain.VisitData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitCountRepository extends JpaRepository<VisitData,Long>, CustomVisitAdminRepository {

    @Query("SELECT SUM(v.visited_count) AS count FROM VisitData v")
    int getAllCount();

}
