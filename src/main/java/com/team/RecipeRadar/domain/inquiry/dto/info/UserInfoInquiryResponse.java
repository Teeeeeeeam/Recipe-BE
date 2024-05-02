package com.team.RecipeRadar.domain.inquiry.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoInquiryResponse {

    private boolean nextPage;

    private List<UserInfoInquiryRequest> content;
}
