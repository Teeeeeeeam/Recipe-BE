package com.team.RecipeRadar.domain.inquiry.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteInquiryDto {

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "문의사항 id", example = "1")
    private Long inquiryId;
}
