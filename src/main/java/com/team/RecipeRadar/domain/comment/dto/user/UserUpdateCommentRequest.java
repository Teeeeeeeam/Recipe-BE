package com.team.RecipeRadar.domain.comment.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
@Data
@NoArgsConstructor
@Schema(name = "댓글 수정 Request")
public class UserUpdateCommentRequest {

    @Schema(description = "댓글 id", example = "1")
    private Long commentId;

    @Schema(description = "수정할 댓글 내용", example = "댓글 수정!")
    @NotBlank(message = "수정할 댓글을 입력해주세요")
    private String commentContent;

}
