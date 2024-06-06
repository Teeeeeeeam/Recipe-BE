package com.team.RecipeRadar.domain.inquiry.dto.user;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import com.team.RecipeRadar.domain.inquiry.dto.InquiryDto;
import lombok.Getter;

import java.util.List;

@Getter
public class InquiryResponse {

    private boolean nextPage;
    private List<InquiryDto> inquiries;

    public InquiryResponse(boolean nextPage, List<InquiryDto> inquiries) {
        this.nextPage = nextPage;
        this.inquiries = inquiries;
    }
}
