package com.team.RecipeRadar.domain.visit.application;

import com.team.RecipeRadar.domain.visit.dao.VisitStatisticsRepository;
import com.team.RecipeRadar.domain.visit.dao.VisitSessionRepository;
import com.team.RecipeRadar.domain.visit.domain.VisitSession;
import com.team.RecipeRadar.domain.visit.dto.DayDto;
import com.team.RecipeRadar.domain.visit.dto.MonthDto;
import com.team.RecipeRadar.domain.visit.dto.WeekDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService{

    private final VisitStatisticsRepository visitStatisticsRepository;
    private final VisitSessionRepository visitSessionRepository;

    @Transactional
    @Override
    public void save(String ipaddress, LocalDateTime expiredAt) {
        visitSessionRepository.save(VisitSession.toEntity(ipaddress, expiredAt));
    }

    @Override
    public boolean ipExists(String ipaddress) {
        boolean  ipAddress = visitSessionRepository.existsByIpAddress(ipaddress);
        if (ipAddress) throw new IllegalStateException("현재 조회된 IP주소 입니다.");
        return !ipAddress;
    }

    /**
     * 일 조회 specificDay null 아니고 ture시에는 14일간의 일간 데이터조회 없을시 기본페이지의 30일간의 데이터 조회
     * @param specificDay
     * @return
     */
    @Override
    public List<DayDto> getDailyVisitCount(Boolean specificDay) {
        return visitStatisticsRepository.getCountDays(specificDay);
    }

    /**
     * 주간 조회 주간의 방문자수를 더한 데이터를 10주간으로 데이터 추출
     * @return
     */
    @Override
    public List<WeekDto> getWeeklyVisitCount() {
        return visitStatisticsRepository.getCountWeek();
    }

    /**
     * 한달간의 방문자수를 집계해 10달간의 데이터를 추출
     * @return
     */
    @Override
    public List<MonthDto> getMonthlyVisitCount() {
        return visitStatisticsRepository.getCountMoth();
    }

    /**
     * 전일 방문자수
     */
    @Override
    public int getPreviousVisitCount() {
        return visitStatisticsRepository.getBeforeCount();
    }

    /**
     * 당일 방문자수
     * @return
     */
    @Override
    public int getCurrentVisitCount() {
        return visitSessionRepository.getCurrentCount();
    }

    /**
     * 지금까지 방문한 사용자의 수
     */
    @Override
    public int getTotalVisitCount() {
        return visitStatisticsRepository.getAllCount();
    }
}
