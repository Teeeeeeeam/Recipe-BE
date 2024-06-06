package com.team.RecipeRadar.domain.questions.dao;

import com.team.RecipeRadar.domain.questions.dto.QuestionDto;

public interface CustomQuestionRepository  {

    QuestionDto details(Long questionId);
}
