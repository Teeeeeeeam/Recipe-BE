package com.team.RecipeRadar.domain.questions.dao;

import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomQuestionRepository  {

    QuestionDto details(Long questionId);

    Slice<QuestionDto> getAllQuestion(Long lasId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable);
}
