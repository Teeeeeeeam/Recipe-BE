package com.team.RecipeRadar.domain.blackList.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackListDto {

    private Long id;
    private String email;
    private Boolean blackCheck;

}
