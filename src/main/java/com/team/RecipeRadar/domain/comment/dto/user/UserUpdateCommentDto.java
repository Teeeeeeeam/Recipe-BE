package com.team.RecipeRadar.domain.comment.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class UserUpdateCommentDto {

    @Schema(description = "수정할 댓글 내용", example = "댓글 수정!")
    private String commentContent;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "댓글 id", example = "1")
    private Long commentId;

    @Schema(hidden = true)
    private LocalDateTime update_At;

    @JsonIgnore
    @JsonCreator
    public UserUpdateCommentDto(@JsonProperty String commentContent,
                                @JsonProperty Long memberId,
                                @JsonProperty Long commentId,
                                @JsonProperty LocalDateTime update_At) {
        this.commentContent = commentContent;
        this.memberId = memberId;
        this.commentId = commentId;
        this.update_At = update_At;
    }
}
