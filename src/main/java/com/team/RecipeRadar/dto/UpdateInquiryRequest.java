package com.team.RecipeRadar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateInquiryRequest {

    private String inquiryTitle;
    private String inquiryContent;
}
