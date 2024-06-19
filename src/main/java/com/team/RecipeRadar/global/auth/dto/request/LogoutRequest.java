package com.team.RecipeRadar.global.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "로그아웃 Request")
public class LogoutRequest {

    private Long memberId;
}
