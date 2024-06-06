package com.team.RecipeRadar.domain.questions.application;

import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;

public interface AnswerService {

    void question_answer(Long questId,QuestionAnswerRequest questionAnswerRequest,String adminNickName);
}
