package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Inquiry;

public class InquiryResponse {

    private final String inquiryTitle;
    private final String inquiryContent;

    public  InquiryResponse(Inquiry inquiry) {
        this.inquiryTitle = inquiry.getInquiryTitle();
        this.inquiryContent = inquiry.getInquiryContent();
    }
}
