package com.team.RecipeRadar.domain.inquiry.dao;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Modifying
    @Query("delete from Inquiry c where c.member.id=:member_id and c.id=:inquiry_id")
    void deleteMemberId(@Param("member_id") Long member_id, @Param("inquiry_id")Long inquiry_id);
}
