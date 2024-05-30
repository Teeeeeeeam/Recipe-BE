package com.team.RecipeRadar.global.visit.api;

import com.team.RecipeRadar.global.exception.advice.ApiControllerAdvice;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.visit.application.VisitService;
import com.team.RecipeRadar.global.visit.dao.VisitRepository;
import com.team.RecipeRadar.global.visit.domain.VisitCount;
import com.team.RecipeRadar.global.visit.domain.VisitData;
import com.team.RecipeRadar.global.visit.dto.DayDto;
import com.team.RecipeRadar.global.visit.dto.MonthDto;
import com.team.RecipeRadar.global.visit.dto.WeekDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "방문자수 조회 컨트롤러" ,description = "현재 프로토타입의 방문자수 조회 API")
@RestController
@RequiredArgsConstructor
public class VisitCountController {

    private final VisitRepository visitRepository;
    private final VisitService visitService;

    @Operation(summary = "방문자수 카운트 API",
            description = "최초 방문시에 해당 API 요청을 보내면 DB에 저장후 쿠키를 반환해준다. 만료시간은 당일 23시59분까지이며  쿠키가 있으면 해당 API 요청을 안보내도됨")
    @PostMapping("/api/visit")
    public ResponseEntity<String> ipCount(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        // 오늘 자정 시간 (UTC)
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
        ZonedDateTime midnightUTC = midnight.atZone(ZoneId.of("UTC"));

        // 현재 시간부터 자정까지의 초 단위 차이 계산
        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnightUTC);
        log.info("sss={}",secondsUntilMidnight);
        String uuid = UUID.randomUUID().toString();
        ResponseCookie responseCookie = ResponseCookie.from("visitors", uuid)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge((int) secondsUntilMidnight)
                .build();
        LocalDateTime db = now.toLocalDate().atStartOfDay().plusDays(1).minusSeconds(5);
        VisitCount entity = VisitCount.toEntity(ipAddress, db);

        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("visitors")){

                boolean existsByIpAddress = visitRepository.existsByIpAddress(ipAddress);
                if(!existsByIpAddress){
                    visitRepository.save(entity);
                }
            }else visitRepository.save(entity);
        }
        log.info("============== 현재 Client ip ======={}",ipAddress);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body("IP Address: " + ipAddress);
    }

    @Operation(summary = "당일방문자 수 조회",description = "당일 방문자수를 조회하는 API")
    @GetMapping("/api/visit-count/today")
    public ResponseEntity<?> todayCount(){
        int currentVisitCount = visitService.getCurrentVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse(true,"성공",currentVisitCount));
    }

    @Operation(summary = "전일 방문자수 조회",description = "전일 방문자 수를 조회하는 API")
    @GetMapping("/api/visit-count/before")
    public ResponseEntity<?> before(){
        int previousVisitCount = visitService.getPreviousVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse(true,"성공",previousVisitCount));
    }

    @Operation(summary = "전체 방문자수 조회",description = "현재 방문자 수를 모두 조회 하는 API")
    @GetMapping("/api/visit-count/all")
    public ResponseEntity<?> apicount2(){
        int totalVisitCount = visitService.getTotalVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"성공",totalVisitCount));
    }

    @Operation(summary = "일간 방문자수 조회",description = "days 보내지않으면 한달간의 사용자의 방문자수를 조회한다, days=true 보내게되면 14일간 방문자수를  조회할수 있다.(티스토리 참고 days안보낼떄는 기본 페이지, ture보낼시에는 방문자 통계의 일간 조회동일)")
    @GetMapping("/api/visit-count/days")
    public ResponseEntity<?> co(@RequestParam(name = "days",required = false) Boolean days){

        List<DayDto> dailyVisitCount = visitService.getDailyVisitCount(days);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"일간 방문자수 조회",dailyVisitCount));
    }
    
    @Operation(summary = "주간 방문자수 조회",description = "1주간의 방문자수를 총10주 조회 일요일 ~토요일기준(현재 해당의 기준으로 주를 계산해 5월1~4일이 1주차로 계산됨")
    @GetMapping("/api/visit-count/week")
    public ResponseEntity<?> week1(){
        List<WeekDto> weeklyVisitCount = visitService.getWeeklyVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"주간 방문자수 조회",weeklyVisitCount));
    }

    @Operation(summary = "월관 방문자수 조회",description = "1주간의 방문자수를 총 10달 조회")
    @GetMapping("/api/visit-count/month")
    public ResponseEntity<?> month(){
        List<MonthDto> monthlyVisitCount = visitService.getMonthlyVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"월간 방문자수 조회",monthlyVisitCount));
    }
}
