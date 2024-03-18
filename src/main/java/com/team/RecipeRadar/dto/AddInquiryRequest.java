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

    public Inquiry toEntity() {
        return Inquiry.builder()
                .inquiryTitle(inquiryTitle)
                .inquiryContent(inquiryContent)
                .build();
    }
}
