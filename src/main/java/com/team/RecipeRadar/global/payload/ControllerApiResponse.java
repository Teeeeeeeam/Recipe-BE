package com.team.RecipeRadar.global.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//api response 형식을 보낼때 사용
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControllerApiResponse<T> {

    @Schema(example = "true")
    private boolean success;

    @Schema(description = "응답 데이터")
    private T message;
}
