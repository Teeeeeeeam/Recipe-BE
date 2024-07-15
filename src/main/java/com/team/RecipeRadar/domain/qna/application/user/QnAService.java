package com.team.RecipeRadar.domain.qna.application.user;


import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import com.team.RecipeRadar.domain.qna.dto.response.QuestionAllResponse;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QnAService {

    void accountQuestion(QuestionRequest questionRequest, MultipartFile file);

    void generalQuestion(QuestionRequest questionRequest, Long memberId, MultipartFile file);

    QuestionAllResponse allUserQuestion(Long lasId, Long memberId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable);
    void deleteQuestions(List<Long> ids, Long memberId);

    QuestionDto viewResponse(MemberDto memberDto, Long questionId);
}
