package com.team.RecipeRadar.domain.qna.application.admin;

import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import com.team.RecipeRadar.domain.qna.dto.response.QuestionAllResponse;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import org.springframework.data.domain.Pageable;

public interface AdminQnAService {

    QuestionDto detailAdminQuestion(Long questionId, Long memberId);

    void questionAnswer(Long questId, QuestionAnswerRequest questionAnswerRequest, String adminNickName);

    QuestionAllResponse allQuestion(Long lasId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable);
}
