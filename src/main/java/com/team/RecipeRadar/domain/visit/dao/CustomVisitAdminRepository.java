package com.team.RecipeRadar.domain.visit.dao;

import com.team.RecipeRadar.domain.visit.dto.DayDto;
import com.team.RecipeRadar.domain.visit.dto.MonthDto;
import com.team.RecipeRadar.domain.visit.dto.WeekDto;

import java.util.List;

public interface CustomVisitAdminRepository {

    List<DayDto> getCountDays(Boolean day);

    List<WeekDto> getCountWeek();

    List<MonthDto> getCountMoth();

    int getBeforeCount();
}
