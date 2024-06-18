package com.team.RecipeRadar.domain.visit.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.userInfo.utils.CookieUtils;
import com.team.RecipeRadar.domain.visit.domain.VisitSession;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.RecipeRadar.domain.visit.application.VisitService;
import com.team.RecipeRadar.domain.visit.dao.VisitSessionRepository;
import com.team.RecipeRadar.domain.visit.dto.DayDto;
import com.team.RecipeRadar.domain.visit.dto.MonthDto;
import com.team.RecipeRadar.domain.visit.dto.WeekDto;
import com.team.mock.CustomMockAdmin;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitCountController.class)
class VisitSessionControllerTest {

    @MockBean VisitSessionRepository visitRepository;
    @MockBean VisitService visitService;
    @MockBean CookieUtils cookieUtils;

    @Autowired private MockMvc mockMvc;

    @MockBean MemberRepository memberRepository;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;

    @Test
    @DisplayName("처음 방문할시에 쿠키 발급후 db에 저장")
    void ipCount() throws Exception {
        // given
        given(visitRepository.existsByIpAddress(anyString())).willReturn(false);
        given(visitRepository.save(any(VisitSession.class))).willReturn(new VisitSession());

        ResponseCookie responseCookie = ResponseCookie.from("visitors", "123.456.789.000").build();
        given(cookieUtils.createCookie(anyString(),anyString(),anyInt())).willReturn(responseCookie);
        // when & then
        mockMvc.perform(post("/api/visit")
                        .header("X-Forwarded-For", "123.456.789.000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("방문 성공"))
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, CoreMatchers.containsString("visitors=")));
    }


    @Test
    @DisplayName("이미 방문한 사용자가 요청시 400 에러")
    void ipCount_Fails() throws Exception {
        when(visitService.ipExists(anyString())).thenThrow(new IllegalStateException("현재 조회된 IP주소 입니다."));

        Cookie cookie = new Cookie("visitors", "fakeCookie");
        mockMvc.perform(post("/api/visit")
                        .cookie(cookie)
                        .header("X-Forwarded-For", "123.456.789.000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("현재 조회된 IP주소 입니다."));
    }

    @Test
    @DisplayName("금일 방문자수 조회")
    @CustomMockAdmin
    void todayCount() throws Exception {

        given(visitService.getCurrentVisitCount()).willReturn(10);

        mockMvc.perform(get("/api/admin/visit-count/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    @DisplayName("전일 방문자수 조회")
    @CustomMockAdmin
    void before() throws Exception {
        given(visitService.getPreviousVisitCount()).willReturn(10);
        mockMvc.perform(get("/api/admin/visit-count/before"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(10));
    }

    @Test
    @DisplayName("전체 방문자수 조회")
    @CustomMockAdmin
    void allCount() throws Exception {

        given(visitService.getTotalVisitCount()).willReturn(100);
        mockMvc.perform(get("/api/admin/visit-count/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(100));
    }
    
    

    @Test
    @CustomMockAdmin
    @DisplayName("일간 방문자수 조회 day null 일떄")
    void days_null() throws Exception {
        boolean day = true;
        List<DayDto> dayDtoList = new ArrayList<>();
        for (int i =0;i<30;i++){
            dayDtoList.add(new DayDto(now().minusDays(i),10));
        }

        given(visitService.getDailyVisitCount(isNull())).willReturn(dayDtoList);

        mockMvc.perform(get("/api/admin/visit-count/days"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(30));

    }

    @Test
    @CustomMockAdmin
    @DisplayName("일간 방문자수 조회 day null 일떄")
    void days() throws Exception {
        boolean day = true;
        List<DayDto> dayDtoList = new ArrayList<>();
        for (int i =0;i<30;i++){
            dayDtoList.add(new DayDto(now().minusDays(i),10));
        }

        given(visitService.getDailyVisitCount(isNull())).willReturn(dayDtoList);

        mockMvc.perform(get("/api/admin/visit-count/days"))
                .andExpect(jsonPath("$.data.size()").value(30));

    }

    @Test
    @CustomMockAdmin
    @DisplayName("일간 방문자수 조회 day true 일떄")
    void days_true() throws Exception {
        List<DayDto> dayDtoList = new ArrayList<>();
        for (int i =0;i<14;i++){
            dayDtoList.add(new DayDto(now().minusDays(i),10));
        }

        given(visitService.getDailyVisitCount(true)).willReturn(dayDtoList);

        mockMvc.perform(get("/api/admin/visit-count/days?days=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(14));

    }
    @Test
    @DisplayName("월간 방문자수 조회")
    @CustomMockAdmin
    void week() throws Exception {
        List<WeekDto> weekDtoList = new ArrayList<>();

        for(int i = 0; i<10;i++){
            weekDtoList.add(new WeekDto(now().minusWeeks(i),10));
        }

        given(visitService.getWeeklyVisitCount()).willReturn(weekDtoList);

        mockMvc.perform(get("/api/admin/visit-count/week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(10));
    }

    @Test
    @DisplayName("월간 방문자수 조회")
    @CustomMockAdmin
    void month() throws Exception {
        List<MonthDto> monthDtoList = new ArrayList<>();
        
        for(int i = 0; i<10;i++){
            monthDtoList.add(new MonthDto(now().minusMonths(i),10));
        }
        
        given(visitService.getMonthlyVisitCount()).willReturn(monthDtoList);
        mockMvc.perform(get("/api/admin/visit-count/month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(10));
    }
}