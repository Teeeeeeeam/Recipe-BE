package com.team.RecipeRadar.domain.visit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayDto {

    private LocalDate date;
    private Integer count;
}
