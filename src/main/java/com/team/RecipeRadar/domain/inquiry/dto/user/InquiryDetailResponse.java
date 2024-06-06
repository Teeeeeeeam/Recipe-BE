package com.team.RecipeRadar.domain.inquiry.dto.user;

import com.team.RecipeRadar.domain.answer.dto.AnswerDto;
import com.team.RecipeRadar.domain.inquiry.dto.InquiryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDetailResponse {

    private InquiryDto inquiry;

    private List<AnswerDto> answers;
}
