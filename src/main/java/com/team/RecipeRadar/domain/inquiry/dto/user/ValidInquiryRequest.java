package com.team.RecipeRadar.domain.inquiry.dto.user;

import lombok.Data;

@Data
public class ValidInquiryRequest {

    private String password;
    private Long inquiryId;
}
