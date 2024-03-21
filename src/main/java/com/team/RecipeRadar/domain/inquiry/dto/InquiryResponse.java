package com.team.RecipeRadar.domain.inquiry.dto;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import lombok.Getter;

@Getter
public class InquiryResponse {

    private final String inquiryTitle;
    private final String inquiryContent;
    private final String inquiryAnswer;
    private final Boolean inquiryAnswered;

    public  InquiryResponse(Inquiry inquiry) {
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryContent = inquiry.getInquiryContent();
        this.inquiryAnswer =  inquiry.getInquiryAnswer();
        this.inquiryAnswered = inquiry.getInquiryAnswered();
    }
}
