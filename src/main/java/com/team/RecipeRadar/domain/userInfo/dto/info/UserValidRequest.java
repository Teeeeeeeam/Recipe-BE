package com.team.RecipeRadar.domain.userInfo.dto.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "사용자 비밀번호 검증 Request",description = "마이페이지 접속시 비밀번호 검증")
public class UserValidRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "일반 사용자일 때만 작성",example = "asdASD12!@")
    String password;

    @Schema(description = "소셜 로그인 사용자일 때만 작성")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String loginId;

    String loginType;
}
