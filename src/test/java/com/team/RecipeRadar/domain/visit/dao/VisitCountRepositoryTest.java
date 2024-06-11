package com.team.RecipeRadar.domain.visit.dao;

import com.team.RecipeRadar.domain.visit.dao.VisitCountRepository;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import com.team.RecipeRadar.domain.visit.domain.VisitData;
import com.team.RecipeRadar.domain.visit.dto.DayDto;
import com.team.RecipeRadar.domain.visit.dto.MonthDto;
import com.team.RecipeRadar.domain.visit.dto.WeekDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@Import(QueryDslConfig.class)
@Transactional
@ActiveProfiles("test")
@Slf4j
class VisitCountRepositoryTest {

    
    @Autowired
    VisitCountRepository visitCountRepository;
    
    @Test
    @DisplayName("총 방문자 수 조회")
    void allVisitedCount(){
        List<VisitData> visitDataList = new ArrayList<>();

        for(int i = 1 ;i<5;i++){
            visitDataList.add(VisitData.builder().days(now()).visited_count(i*10).build());
        }

        visitCountRepository.saveAll(visitDataList);
        int allCount = visitCountRepository.getAllCount();
        assertThat(allCount).isEqualTo(100);
    }


    @Test
    @DisplayName("전날 방문자수 조회")
    void dayVisitedCount(){
        VisitData before = VisitData.builder().days(now().minusDays(1)).visited_count(100).build();
        VisitData now = VisitData.builder().days(now()).visited_count(100).build();
        visitCountRepository.save(before);
        visitCountRepository.save(now);

        int beforeCount = visitCountRepository.getBeforeCount();

        assertThat(beforeCount).isEqualTo(100);
    }
    
    @Test
    @DisplayName("일간 방문자수 조회")
    void daysCount(){
        List<VisitData> visitDataList = new ArrayList<>();
        for(long i =1; i<30;i++){
            visitDataList.add(VisitData.builder().days(now().minusDays(i)).visited_count(10).build());
        }
        visitCountRepository.save(VisitData.builder().days(now()).visited_count(11).build());
        visitCountRepository.saveAll(visitDataList);


        List<DayDto> countDays = visitCountRepository.getCountDays(null);
        List<DayDto> countDays_ture = visitCountRepository.getCountDays(true);


        // null 로 들어올떄는 기본 30개를 보여주고
        assertThat(countDays).hasSize(30);
        // true로 들어오면 14일전 데이터를 보여준다.
        assertThat(countDays_ture).hasSize(14);

    }

    @Test
    @DisplayName("주간 방문자수 조회")
    void weekCount(){
        List<VisitData> visitDataList = new ArrayList<>();
        for(int i =1;i<20;i++){
            visitDataList.add(VisitData.builder().days(now().minusWeeks(i)).visited_count(10).build());
        }
        visitCountRepository.save(VisitData.builder().days(now()).visited_count(11).build());       //당일
        
        visitCountRepository.saveAll(visitDataList);

        List<WeekDto> countWeek = visitCountRepository.getCountWeek();

        //데이터가 21개이지만 10주전의 데이터를 구하기때문에 10개만 추출
        assertThat(countWeek).hasSize(10);      
        assertThat(countWeek.get(2).count).isEqualTo(10);
    }

    @Test
    @DisplayName("월간 방문자수 조회")
    void MonthCount(){
        List<VisitData> visitDataList = new ArrayList<>();
        for(int i =1;i<20;i++){     //20년전 까지의 데이터 저장
            visitDataList.add(VisitData.builder().days(now().minusMonths(i)).visited_count(10).build());
        }
        visitCountRepository.save(VisitData.builder().days(now()).visited_count(11).build());       //당일

        visitCountRepository.saveAll(visitDataList);

        List<MonthDto> countMoth = visitCountRepository.getCountMoth();

        //데이터가 21달의 데이데이터가 존재하지만 10달의 데이터를 구하기때문에 10개만 추출
        assertThat(countMoth).hasSize(10);
        assertThat(countMoth.get(2).count).isEqualTo(10);
    }

}