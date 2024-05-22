package com.team.RecipeRadar.domain.post.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDeleteRequest {

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "요리글 id", example = "1")
    private Long postId;
}
