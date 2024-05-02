package com.team.RecipeRadar.domain.member.dto.valid;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NicknameValidRequest {

    @Schema(description = "사용자 닉네임", example = "나만냉장고")
    private String nickName;
}
