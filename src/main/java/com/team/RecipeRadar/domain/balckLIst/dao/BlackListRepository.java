package com.team.RecipeRadar.domain.balckLIst.dao;

import com.team.RecipeRadar.domain.balckLIst.domain.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList,Long>, CustomBlackRepository {

    boolean existsByEmail(String email);

}
