package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Inquiry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddInquiryRequest {
    private String inquiryTitle;
    private String inquiryContent;
    private String inquiryAnswer;
    private Boolean inquiryAnswered;

    public Inquiry toEntity() {
        return Inquiry.builder()
                .inquiryAnswered(inquiryAnswered)
                .inquiryAnswer(inquiryAnswer)
                .inquiryTitle(inquiryTitle)
                .inquiryContent(inquiryContent)
                .build();
    }
}
