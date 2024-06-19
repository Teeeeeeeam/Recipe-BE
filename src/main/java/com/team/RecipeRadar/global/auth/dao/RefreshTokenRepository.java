package com.team.RecipeRadar.global.auth.dao;

import com.team.RecipeRadar.global.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Boolean existsByRefreshTokenAndMemberLoginId(String refreshToken ,String loginId);

    @Modifying
    @Query("delete from RefreshToken rt where rt.member.id=:member_id")
    void DeleteByMemberId(@Param("member_id")Long member_id);

    RefreshToken findByMemberId(Long memberId);

}
