package com.team.RecipeRadar.domain.member.dto.rqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "이메일 검증 Request")
public class EmailValidRequest {

    @Schema(description = "이메일 검증", example = "자신이메일@naver.com")
    private String email;
}
