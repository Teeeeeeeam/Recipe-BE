package com.team.RecipeRadar.domain.answer.dto.admin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class AdminAddAnswerDto {

    @Schema(description = "댓글 내용", example = "댓글 작성!")
    @NotBlank(message = "댓글을 입력해주세요")
    private String answerContent;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "문의사항 id", example = "1")
    private Long inquiryId;

    @Schema(hidden = true)
    private LocalDateTime created_at;


    @JsonIgnore
    @JsonCreator
    public AdminAddAnswerDto(@JsonProperty("answerContent") String answerContent,
                             @JsonProperty("memberId") Long memberId,
                             @JsonProperty("inquiryId") Long inquiryId,
                             @JsonProperty("create_at")LocalDateTime created_at) {
        this.answerContent = answerContent;
        this.memberId = memberId;
        this.inquiryId = inquiryId;
        this.created_at = created_at;
    }
}
