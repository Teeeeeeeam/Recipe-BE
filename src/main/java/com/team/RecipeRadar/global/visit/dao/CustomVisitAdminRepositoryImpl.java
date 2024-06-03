package com.team.RecipeRadar.global.visit.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.global.visit.dto.DayDto;
import com.team.RecipeRadar.global.visit.dto.MonthDto;
import com.team.RecipeRadar.global.visit.dto.WeekDto;
import com.team.RecipeRadar.global.visit.domain.VisitData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.global.visit.domain.QVisitData.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomVisitAdminRepositoryImpl implements CustomVisitAdminRepository {

    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 일간 방문자수를 조회한다. day 값이 TURE 일때에는 14일간의 일간데이터를 조회하며 null 일떄에는 30일간의데이터를 조회(메인 페이지)
     */
    public List<DayDto> getCountDays(Boolean day) {
        BooleanBuilder builder = new BooleanBuilder();

        if (day != null) {       //일간조회, 2주전까지 가능;
            if (day) {
                LocalDateTime startDay = LocalDateTime.now().minusDays(14).withHour(0).withMinute(0);
                LocalDateTime endDateTime = LocalDateTime.now();

                builder.and(visitData.days.goe(startDay))       // >=
                        .and(visitData.days.loe(endDateTime));       // <=
            }
        } else if (day == null) {        // 아무 값도 입력하지 않았으면 현재 선택한 달에 한달만 보여짐, 기본
            LocalDate firstDayOfThisMonth = LocalDate.now().withDayOfMonth(1);
            LocalDateTime dateTime = LocalDateTime.of(firstDayOfThisMonth, LocalTime.MIN);

            LocalDateTime now = LocalDateTime.now();
            builder.and(visitData.days.goe(dateTime))
                    .and(visitData.days.loe(now));
        }

        List<VisitData> result = jpaQueryFactory.select(visitData)
                .from(visitData)
                .where(builder)
                .orderBy(visitData.days.desc())
                .fetch();

        List<DayDto> dayDtos = result.stream().map(v -> new DayDto(LocalDate.of(v.getDays().getYear(),v.getDays().getMonth(),v.getDays().getMonthValue()), v.getVisited_count())).collect(Collectors.toList());
        return dayDtos;
    }

    //하루전날 방문자수 조회
    @Override
    public int getBeforeCount() {
        LocalDateTime previousDay = LocalDateTime.now().minusDays(1); // 하루 전날

        VisitData visitedAdmin1 = jpaQueryFactory.selectFrom(visitData)
                .where(visitData.days.year().eq(previousDay.getYear())
                        .and(visitData.days.month().eq(previousDay.getMonthValue()))
                        .and(visitData.days.dayOfMonth().eq(previousDay.getDayOfMonth())))
                .fetchOne();
        return visitedAdmin1.getVisited_count();
    }

    /**
     * 주간 조회수 조회 1주간의 데이터를 총 10주간의 방문자수의 합을 조회한다. (일요일기준으로 1주일)
     *
     * @return
     */
    /*
    SELECT
        visited_admin.days AS week_start,
        SUM(visited_count) AS total_visited_count
    FROM
        visited_admin
    WHERE
        days >= DATE_SUB(CURDATE(), INTERVAL 10 WEEK) -- 오늘 기준 10주 전부터의 데이터를 조회
        AND days <= CURDATE() -- 오늘까지의 데이터만을 조회
    GROUP BY
        YEARWEEK(days)
    ORDER BY
        week_start DESC
    LIMIT 10;
     */
    public List<WeekDto> getCountWeek() {
        LocalDateTime dateTime = LocalDateTime.now().minusWeeks(10);        //10주전

        List<Tuple> list = jpaQueryFactory.select(
                        visitData.days,
                        visitData.visited_count.sum()
                ).from(visitData)
                .where(visitData.days.goe(dateTime).and(visitData.days.loe(LocalDateTime.now())))
                .groupBy(visitData.days.yearWeek())
                .orderBy(visitData.days.desc())
                .limit(10)
                .fetch();

        List<WeekDto> weekDtoList = list.stream().map(tuple -> new WeekDto(toLocalDate(tuple), tuple.get(visitData.visited_count.sum()))).collect(Collectors.toList());

        return weekDtoList;
    }

    /**
     * 월간 단위 합계 조회 한달단위로 10개월간의데이터를 조회한다. 한달동안 방문한사용자의 총 데이터를 조회한다.
     *
     * @return
     */
    /*
    SELECT
        visited_admin.days
        SUM(visited_count) AS total_visited_count
    FROM
        visited_admin
    WHERE
        days >= DATE_SUB(CURDATE(), INTERVAL 10 YEAR)
        AND days <= CURDATE()
    GROUP BY
        YEAR(days), MONTH(days)
    ORDER BY
        visited_admin.days DESC
    LIMIT 10;

     */
    @Override
    public List<MonthDto> getCountMoth() {
        LocalDateTime dateTime = LocalDateTime.now().minusYears(10);
        List<Tuple> list = jpaQueryFactory.select(
                        visitData.days,
                        visitData.visited_count.sum()
                ).from(visitData)
                .where(visitData.days.goe(dateTime).and(visitData.days.loe(LocalDateTime.now())))
                .groupBy(visitData.days.year(), visitData.days.month())
                .orderBy(visitData.days.desc())
                .limit(10)
                .fetch();

        List<MonthDto> collect1 = list.stream().map(tuple -> new MonthDto(toLocalDate(tuple), tuple.get(visitData.visited_count.sum()))).collect(Collectors.toList());

        return collect1;
    }



    //LocalDateTime -> LocalDate로 변환
    static LocalDate toLocalDate(Tuple tuple) {
        LocalDateTime dateTime = tuple.get(visitData.days);

        return LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth());
    }

}