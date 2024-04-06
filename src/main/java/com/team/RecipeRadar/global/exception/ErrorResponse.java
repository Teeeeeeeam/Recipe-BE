package com.team.RecipeRadar.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse<T> {

    @Schema(defaultValue = "false")
    private boolean success;
    private T message;
}
