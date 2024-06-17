package com.team.RecipeRadar.domain.visit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitedAdminDto {

    private Integer count;
    private String dateTimes;
}
