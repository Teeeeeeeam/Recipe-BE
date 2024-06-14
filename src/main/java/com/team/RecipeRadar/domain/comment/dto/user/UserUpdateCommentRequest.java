package com.team.RecipeRadar.domain.comment.dto.user;

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
@Schema(name = "댓글 수정 Request")
public class UserUpdateCommentRequest {

    @Schema(description = "수정할 댓글 내용", example = "댓글 수정!")
    @NotBlank(message = "수정할 댓글을 입력해주세요")
    private String commentContent;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "댓글 id", example = "1")
    private Long commentId;

    @Schema(hidden = true)
    private LocalDateTime updateAt;

    @JsonIgnore
    @JsonCreator
    public UserUpdateCommentRequest(@JsonProperty String commentContent,
                                    @JsonProperty Long memberId,
                                    @JsonProperty Long commentId,
                                    @JsonProperty LocalDateTime updateAt) {
        this.commentContent = commentContent;
        this.memberId = memberId;
        this.commentId = commentId;
        this.updateAt = updateAt;
    }
}
