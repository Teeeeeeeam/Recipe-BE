package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Inquiry;
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
