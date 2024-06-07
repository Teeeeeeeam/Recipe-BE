package com.team.RecipeRadar.domain.questions.dao;

import com.team.RecipeRadar.domain.questions.dto.QuestionDto;

public interface CustomAnswerRepository {

    QuestionDto viewResponse(Long questionId);
}
