package com.team.RecipeRadar.domain.userInfo.dto.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "사용자 이메일 변겅 Request")
public class UserInfoEmailRequest {

    @Schema(example = "keuye06380618@gmail.com")
    private String email;

    @Schema(example = "123456")
    private String code;

    @Schema(example = "user1234")
    private String loginId;

    @Schema(example = "normal")
    private String loginType;

}
