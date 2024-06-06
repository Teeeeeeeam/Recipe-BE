package com.team.RecipeRadar.domain.answer.application;

import com.team.RecipeRadar.domain.answer.domain.Answer;
import com.team.RecipeRadar.domain.answer.dto.admin.AdminAddAnswerDto;
import com.team.RecipeRadar.domain.answer.dto.admin.AdminDeleteAnswerDto;
import com.team.RecipeRadar.domain.comment.domain.Comment;

public interface AnswerService {

    Answer save(AdminAddAnswerDto userAddAnswerDto);

    void delete_answer(AdminDeleteAnswerDto adminDeleteAnswerDto);

    void update(Long member_id,Long answer_id,String Content);

    Answer findById(Long id);
    
}
