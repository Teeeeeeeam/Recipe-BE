package com.team.RecipeRadar.domain.questions.dao;

import com.team.RecipeRadar.domain.questions.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long>, CustomQuestionRepository {
}
