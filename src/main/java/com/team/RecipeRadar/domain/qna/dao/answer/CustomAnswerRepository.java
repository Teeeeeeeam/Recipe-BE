package com.team.RecipeRadar.domain.qna.dao.answer;

import com.team.RecipeRadar.domain.qna.dto.QuestionDto;

public interface CustomAnswerRepository {

    QuestionDto viewResponse(Long questionId);

    void deleteByQuestionIdWithMember(Long memberId);
}
