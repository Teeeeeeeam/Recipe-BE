package com.team.RecipeRadar.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Schema(name = "댓글 등록 Request")
public class UserAddCommentRequest {

    @Schema(description = "게시글 id", example = "1")
    private Long postId;

    @Schema(description = "댓글 내용", example = "댓글 작성!")
    @NotBlank(message = "댓글을 입력해주세요")
    private String commentContent;

}
