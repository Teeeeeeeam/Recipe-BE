package com.team.RecipeRadar.global.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//api response 형식을 보낼때 사용
@Data
@Builder
@NoArgsConstructor
public class ControllerApiResponse<T> {

    @Schema(example = "true")
    private boolean success;

    @Schema(description = "응답 메세지")
    private String message;

    @Schema(description = "응답 데이터 컬렉션 형태")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ControllerApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ControllerApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
