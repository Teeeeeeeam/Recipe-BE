package com.team.RecipeRadar.domain.qna.dao.question;

import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomQuestionRepository  {

    QuestionDto details(Long questionId);

    Slice<QuestionDto> getAllQuestion(Long lasId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable);

    Slice<QuestionDto> getUserAllQuestion(Long lastId, Long memberId,QuestionType questionType, QuestionStatus questionStatus, Pageable pageable);
}
