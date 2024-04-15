package com.team.RecipeRadar.domain.inquiry.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserUpdateInquiryDto {

    @Schema(description = "수정할 문의사항 제목", example = "문의사항 제목 수정!")
    private String inquiryTitle;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "문의사항 id", example = "1")
    private Long inquiryId;

    @Schema(hidden = true)
    private LocalDateTime update_At;

    @JsonIgnore
    @JsonCreator
    public UserUpdateInquiryDto(@JsonProperty("inquiryTitle") String inquiryTitle,
                                @JsonProperty("memberId") Long memberId,
                                @JsonProperty("inquiryId") Long inquiryId
                                ) {
        this.inquiryTitle = inquiryTitle;
        this.memberId = memberId;
        this.inquiryId = inquiryId;
    }
}
