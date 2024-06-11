package com.team.RecipeRadar.domain.comment.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "댓글 삭제 Request")
public class UserDeleteCommentRequest {

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "댓글 id", example = "1")
    private Long commentId;

}
