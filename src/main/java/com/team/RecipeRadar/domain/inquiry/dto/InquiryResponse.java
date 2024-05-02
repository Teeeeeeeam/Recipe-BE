package com.team.RecipeRadar.domain.inquiry.dto;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import lombok.Getter;

@Getter
public class InquiryResponse {

    private final String inquiryTitle;

    public InquiryResponse(Inquiry inquiry) {
        this.inquiryTitle = inquiry.getInquiryTitle();
    }
}
