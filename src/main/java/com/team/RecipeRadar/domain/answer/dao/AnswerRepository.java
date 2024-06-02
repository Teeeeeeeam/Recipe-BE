package com.team.RecipeRadar.domain.answer.dao;

import com.team.RecipeRadar.domain.answer.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Modifying
    @Query("delete from Answer c where c.member.id=:member_id and c.id=:answer_id")
    void deleteMemberId(@Param("member_id") Long member_id, @Param("answer_id")Long answer_id);

    @Modifying
    @Query("delete from Answer c where c.inquiry.id=:inquiryId")
    void deleteInquiryID(@Param("inquiryId")Long inquiryId);
}
