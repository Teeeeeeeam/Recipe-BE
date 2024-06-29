package com.team.RecipeRadar.domain.visit.dao;

import com.team.RecipeRadar.domain.visit.domain.VisitStatistics;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import com.team.RecipeRadar.domain.visit.dto.DayDto;
import com.team.RecipeRadar.domain.visit.dto.MonthDto;
import com.team.RecipeRadar.domain.visit.dto.WeekDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class VisitStatisticsRepositoryTest {

    @Autowired VisitStatisticsRepository visitStatisticsRepository;
    
    @Test
    @DisplayName("총 방문자 수 조회")
    void allVisitedCount(){
        List<VisitStatistics> visitDataList = new ArrayList<>();

        for(int i = 1 ;i<5;i++){
            visitDataList.add(VisitStatistics.builder().day(now()).visitCount(i*10).build());
        }

        visitStatisticsRepository.saveAll(visitDataList);
        int allCount = visitStatisticsRepository.getAllCount();
        assertThat(allCount).isEqualTo(100);
    }


    @Test
    @DisplayName("전날 방문자수 조회")
    void dayVisitedCount(){
        VisitStatistics before = VisitStatistics.builder().day(now().minusDays(1)).visitCount(100).build();
        VisitStatistics now = VisitStatistics.builder().day(now()).visitCount(100).build();
        visitStatisticsRepository.save(before);
        visitStatisticsRepository.save(now);

        int beforeCount = visitStatisticsRepository.getBeforeCount();

        assertThat(beforeCount).isEqualTo(100);
    }
    
    @Test
    @DisplayName("일간 방문자수 조회")
    void daysCount(){
        List<VisitStatistics> visitDataList = new ArrayList<>();
        for(long i =1; i<30;i++){
            visitDataList.add(VisitStatistics.builder().day(now().minusDays(i)).visitCount(10).build());
        }
        visitStatisticsRepository.save(VisitStatistics.builder().day(now()).visitCount(11).build());
        visitStatisticsRepository.saveAll(visitDataList);


        List<DayDto> countDays = visitStatisticsRepository.getCountDays(null);
        List<DayDto> countDays_ture = visitStatisticsRepository.getCountDays(true);


        // null 로 들어올떄는 기본 30개를 보여주고
        assertThat(countDays).hasSize(30);
        // true로 들어오면 14일전 데이터를 보여준다.
        assertThat(countDays_ture).hasSize(14);

    }

    @Test
    @DisplayName("주간 방문자수 조회")
    void weekCount(){
        List<VisitStatistics> visitDataList = new ArrayList<>();
        for(int i =1;i<20;i++){
            visitDataList.add(VisitStatistics.builder().day(now().minusWeeks(i)).visitCount(10).build());
        }
        visitStatisticsRepository.save(VisitStatistics.builder().day(now()).visitCount(11).build());       //당일
        
        visitStatisticsRepository.saveAll(visitDataList);

        List<WeekDto> countWeek = visitStatisticsRepository.getCountWeek();

        //데이터가 21개이지만 10주전의 데이터를 구하기때문에 10개만 추출
        assertThat(countWeek).hasSize(10);      
        assertThat(countWeek.get(2).count).isEqualTo(10);
    }

    @Test
    @DisplayName("월간 방문자수 조회")
    void MonthCount(){
        List<VisitStatistics> visitDataList = new ArrayList<>();
        for(int i =1;i<20;i++){     //20년전 까지의 데이터 저장
            visitDataList.add(VisitStatistics.builder().day(now().minusMonths(i)).visitCount(10).build());
        }
        visitStatisticsRepository.save(VisitStatistics.builder().day(now()).visitCount(11).build());       //당일

        visitStatisticsRepository.saveAll(visitDataList);

        List<MonthDto> countMoth = visitStatisticsRepository.getCountMoth();

        //데이터가 21달의 데이데이터가 존재하지만 10달의 데이터를 구하기때문에 10개만 추출
        assertThat(countMoth).hasSize(10);
        assertThat(countMoth.get(2).count).isEqualTo(10);
    }

}