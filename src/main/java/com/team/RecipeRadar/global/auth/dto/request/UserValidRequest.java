package com.team.RecipeRadar.global.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "사용자 비밀번호 검증 Request",description = "마이페이지 접속시 비밀번호 검증")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserValidRequest {

    @Schema(description = "일반 사용자일 때만 작성",example = "asdASD12!@")
    private String password;
}
