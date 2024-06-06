package com.team.RecipeRadar.global.visit.dao;

import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import com.team.RecipeRadar.global.visit.domain.VisitCount;
import com.team.RecipeRadar.global.visit.domain.VisitData;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@Import(QueryDslConfig.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Slf4j
class VisitRepositoryTest {

    @Autowired VisitRepository visitRepository;

    @Test
    @DisplayName("일간 조회수 카운트")
    void dayCount(){
        List<VisitCount> visitCountList = new ArrayList<>();
        for(int i = 0; i< 10;i++) {
            visitCountList.add(VisitCount.toEntity(String.valueOf(i), now()));
        }
        VisitCount entity = VisitCount.toEntity("ip", now().plusDays(1));
        visitRepository.save(entity);
        visitRepository.saveAll(visitCountList);

        Integer currentCount = visitRepository.getCurrentCount();

        //총 카운트는 11일지만 현재날자 기준으로 하기떄문에 10개
        assertThat(currentCount).isEqualTo(10);
    }

    @Test
    @DisplayName("IP가 존재하는지 유무 테스트")
    void existIpAddress(){
        String ipAddress = "111.11.1";
        VisitCount entity = VisitCount.toEntity(ipAddress, now());
        VisitCount entity1 = VisitCount.toEntity("111.11.2",now());

        visitRepository.save(entity);
        visitRepository.save(entity1);

        boolean existsByIpAddress = visitRepository.existsByIpAddress(ipAddress);
        boolean noExistsByIpAddress = visitRepository.existsByIpAddress("1123,.123");

        assertThat(existsByIpAddress).isTrue();
        assertThat(noExistsByIpAddress).isFalse();

    }

}