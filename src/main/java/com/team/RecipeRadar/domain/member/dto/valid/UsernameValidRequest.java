package com.team.RecipeRadar.domain.member.dto.valid;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UsernameValidRequest {

    @Schema(description = "사용자 실명", example = "홍길동")
    private String username;
}
