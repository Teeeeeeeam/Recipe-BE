package com.team.RecipeRadar.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//api response 형식을 보낼때 사용
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    private Object message;
}
