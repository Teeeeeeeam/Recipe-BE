package com.team.RecipeRadar.global.visit.application;

import com.team.RecipeRadar.global.visit.dto.DayDto;
import com.team.RecipeRadar.global.visit.dto.MonthDto;
import com.team.RecipeRadar.global.visit.dto.WeekDto;

import java.util.List;

public interface VisitService {

    List<DayDto> getDailyVisitCount(Boolean specificDay);

    List<WeekDto> getWeeklyVisitCount();

    List<MonthDto> getMonthlyVisitCount();

    int getPreviousVisitCount();

    int getCurrentVisitCount();

    int getTotalVisitCount();

}
