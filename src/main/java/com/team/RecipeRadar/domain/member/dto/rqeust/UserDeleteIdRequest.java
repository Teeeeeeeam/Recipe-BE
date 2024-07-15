package com.team.RecipeRadar.domain.member.dto.rqeust;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "사용자 탈퇴 Request")
public class UserDeleteIdRequest {

    @Schema(defaultValue ="false")
    boolean checkBox;
}
