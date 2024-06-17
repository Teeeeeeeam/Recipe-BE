package com.team.RecipeRadar.domain.questions.application;


import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import com.team.RecipeRadar.domain.questions.dto.QuestionAllResponse;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {

    void accountQuestion(QuestionRequest questionRequest, MultipartFile file);

    void generalQuestion(QuestionRequest questionRequest, Long memberId, MultipartFile file);

    QuestionDto detailAdminQuestion(Long questionId, Long memberId);

    QuestionAllResponse allQuestion(Long lasId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable);

    QuestionAllResponse allUserQuestion(Long lasId, Long memberId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable);
    void deleteQuestions(List<Long> ids, Long memberId);
}
