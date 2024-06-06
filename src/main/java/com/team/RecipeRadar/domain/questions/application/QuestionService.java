package com.team.RecipeRadar.domain.questions.application;


import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {

    void account_Question(QuestionRequest questionRequest, MultipartFile file);

    void general_Question(QuestionRequest questionRequest, MultipartFile file);

    QuestionDto detailAdmin_Question(Long questionId,String loginId);
}
