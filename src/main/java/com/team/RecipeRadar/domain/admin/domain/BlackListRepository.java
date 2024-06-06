package com.team.RecipeRadar.domain.admin.domain;

import com.team.RecipeRadar.domain.admin.dao.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList,Long> {

    boolean existsByEmail(String email);
}
