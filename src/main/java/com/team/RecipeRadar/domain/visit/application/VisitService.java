package com.team.RecipeRadar.domain.visit.application;

import com.team.RecipeRadar.domain.visit.dto.DayDto;
import com.team.RecipeRadar.domain.visit.dto.MonthDto;
import com.team.RecipeRadar.domain.visit.dto.WeekDto;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitService {

    void save(String ipaddress, LocalDateTime expiredAt);

    boolean ipExists(String ipaddress);

    List<DayDto> getDailyVisitCount(Boolean specificDay);

    List<WeekDto> getWeeklyVisitCount();

    List<MonthDto> getMonthlyVisitCount();

    int getPreviousVisitCount();

    int getCurrentVisitCount();

    int getTotalVisitCount();

}
