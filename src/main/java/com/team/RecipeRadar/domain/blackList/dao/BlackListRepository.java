package com.team.RecipeRadar.domain.blackList.dao;

import com.team.RecipeRadar.domain.blackList.domain.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList,Long>, CustomBlackRepository {

    boolean existsByEmail(String email);

}
