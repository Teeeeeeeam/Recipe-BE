package com.team.RecipeRadar.domain.questions.dao;

import com.team.RecipeRadar.domain.questions.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer,Long>,CustomAnswerRepository {

    void deleteByQuestionId(Long questionId);
}
