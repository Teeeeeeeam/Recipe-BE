package com.team.RecipeRadar.domain.answer.dto.admin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AdminUpdateAnswerDto {

    @Schema(description = "수정할 댓글 내용", example = "댓글 수정!")
    @NotBlank(message = "수정할 댓글을 입력해주세요")
    private String answerContent;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "댓글 id", example = "1")
    private Long answerId;

    @Schema(hidden = true)
    private LocalDateTime update_At;

    @JsonIgnore
    @JsonCreator
    public AdminUpdateAnswerDto(@JsonProperty String answerContent,
                                @JsonProperty Long memberId,
                                @JsonProperty Long answerId,
                                @JsonProperty LocalDateTime update_At) {
        this.answerContent = answerContent;
        this.memberId = memberId;
        this.answerId = answerId;
        this.update_At = update_At;
    }
}
