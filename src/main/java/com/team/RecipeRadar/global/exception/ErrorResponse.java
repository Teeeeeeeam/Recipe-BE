package com.team.RecipeRadar.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@Schema(name = "오류 응답 Response")
public class ErrorResponse<T> {

    @Schema(defaultValue = "false")
    private boolean success;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ErrorResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }


    public ErrorResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
