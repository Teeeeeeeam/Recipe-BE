package com.team.RecipeRadar.domain.inquiry.dto.info;

import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoInquiryRequest {

    private Long id;

    private String inquiryTitle;

    public static UserInfoInquiryRequest of(Inquiry inquiry) {
        return new UserInfoInquiryRequest(inquiry.getId(),inquiry.getInquiryTitle());
    }
}

