package com.team.RecipeRadar.domain.visit.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.visit.domain.VisitStatistics;
import com.team.RecipeRadar.domain.visit.dto.DayDto;
import com.team.RecipeRadar.domain.visit.dto.MonthDto;
import com.team.RecipeRadar.domain.visit.dto.WeekDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.visit.domain.QVisitStatistics.*;


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
                LocalDateTime startDay = LocalDateTime.now().minusDays(13).withHour(0).withMinute(0);
                LocalDateTime endDateTime = LocalDateTime.now();

                builder.and(visitStatistics.day.goe(startDay))       // >=
                        .and(visitStatistics.day.loe(endDateTime));       // <=
            }
        } else {        // 아무 값도 입력하지 않았으면 현재 선택한 달에 한달만 보여짐, 기본
            LocalDate firstDayOfThisMonth = LocalDate.now().minusDays(30);
            LocalDateTime dateTime = LocalDateTime.of(firstDayOfThisMonth, LocalTime.MIN);

            LocalDateTime now = LocalDateTime.now();
            builder.and(visitStatistics.day.goe(dateTime))
                    .and(visitStatistics.day.loe(now));
        }

        List<VisitStatistics> result = jpaQueryFactory.select(visitStatistics)
                .from(visitStatistics)
                .where(builder)
                .orderBy(visitStatistics.day.desc())
                .fetch();

        List<DayDto> dayDtos = result.stream().map(v -> new DayDto(LocalDate.of(v.getDay().getYear(),v.getDay().getMonth(),v.getDay().getDayOfMonth()), v.getVisitCount())).collect(Collectors.toList());
        return dayDtos;
    }

    //하루전날 방문자수 조회
    @Override
    public int getBeforeCount() {
        LocalDateTime previousDay = LocalDateTime.now().minusDays(1); // 하루 전날

        VisitStatistics visitedAdmin1 = jpaQueryFactory.selectFrom(visitStatistics)
                .where(visitStatistics.day.year().eq(previousDay.getYear())
                        .and(visitStatistics.day.month().eq(previousDay.getMonthValue()))
                        .and(visitStatistics.day.dayOfMonth().eq(previousDay.getDayOfMonth())))
                .fetchOne();
        return visitedAdmin1.getVisitCount();
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
                        visitStatistics.day,
                        visitStatistics.visitCount.sum()
                ).from(visitStatistics)
                .where(visitStatistics.day.goe(dateTime).and(visitStatistics.day.loe(LocalDateTime.now())))
                .groupBy(visitStatistics.day.yearWeek())
                .orderBy(visitStatistics.day.desc())
                .limit(10)
                .fetch();

        List<WeekDto> weekDtoList = list.stream().map(tuple -> new WeekDto(toLocalDate(tuple), tuple.get(visitStatistics.visitCount.sum()))).collect(Collectors.toList());

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
                        visitStatistics.day,
                        visitStatistics.visitCount.sum()
                ).from(visitStatistics)
                .where(visitStatistics.day.goe(dateTime).and(visitStatistics.day.loe(LocalDateTime.now())))
                .groupBy(visitStatistics.day.year(), visitStatistics.day.month())
                .orderBy(visitStatistics.day.desc())
                .limit(10)
                .fetch();

        List<MonthDto> collect1 = list.stream().map(tuple -> new MonthDto(toLocalDate(tuple), tuple.get(visitStatistics.visitCount.sum()))).collect(Collectors.toList());

        return collect1;
    }



    //LocalDateTime -> LocalDate로 변환
    static LocalDate toLocalDate(Tuple tuple) {
        LocalDateTime dateTime = tuple.get(visitStatistics.day);

        return LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth());
    }

}