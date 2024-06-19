package com.team.RecipeRadar.domain.qna.dao.question;

import com.team.RecipeRadar.domain.qna.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long>, CustomQuestionRepository {
    void deleteAllByMemberId(Long memberId);
}
