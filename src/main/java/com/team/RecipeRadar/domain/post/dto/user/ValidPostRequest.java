package com.team.RecipeRadar.domain.post.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "게시글 비밀번호 검증 Request")
public class ValidPostRequest {

    @Schema(example = "123456")
    private String password;

    @Schema(example = "1")
    private Long postId;
}
