package com.team.RecipeRadar.global.visit.dao;

import com.team.RecipeRadar.global.visit.dto.DayDto;
import com.team.RecipeRadar.global.visit.dto.MonthDto;
import com.team.RecipeRadar.global.visit.dto.WeekDto;
import java.util.List;

public interface CustomVisitAdminRepository {

    List<DayDto> getCountDays(Boolean day);

    List<WeekDto> getCountWeek();

    List<MonthDto> getCountMoth();

    int getBeforeCount();
}
