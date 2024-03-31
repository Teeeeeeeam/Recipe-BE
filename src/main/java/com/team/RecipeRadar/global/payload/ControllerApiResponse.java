package com.team.RecipeRadar.global.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//api response 형식을 보낼때 사용
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControllerApiResponse {

    private boolean success;
    private Object message;
}
