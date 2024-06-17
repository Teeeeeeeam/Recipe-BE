package com.team.RecipeRadar.domain.questions.application;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;

public interface AnswerService {

    void questionAnswer(Long questId, QuestionAnswerRequest questionAnswerRequest, String adminNickName);

    QuestionDto viewResponse(MemberDto memberDto, Long questionId);
}
