package com.team.RecipeRadar.domain.answer.application;

import com.team.RecipeRadar.domain.answer.domain.Answer;
import com.team.RecipeRadar.domain.answer.dto.admin.AdminAddAnswerDto;
import com.team.RecipeRadar.domain.answer.dto.admin.AdminDeleteAnswerDto;

public interface AnswerService {

    Answer save(AdminAddAnswerDto userAddAnswerDto);

    void delete_answer(AdminDeleteAnswerDto adminDeleteAnswerDto);

    void update(Long member_id,Long answer_id,String Content);

    
}
