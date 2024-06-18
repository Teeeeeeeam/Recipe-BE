package com.team.RecipeRadar.domain.visit.api;

import com.team.RecipeRadar.domain.userInfo.utils.CookieUtils;
import com.team.RecipeRadar.domain.visit.application.VisitService;
import com.team.RecipeRadar.domain.visit.dto.DayDto;
import com.team.RecipeRadar.domain.visit.dto.MonthDto;
import com.team.RecipeRadar.domain.visit.dto.WeekDto;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Tag(name = "어드민 - 방문자수 컨트롤러" ,description = "방문한 사용자의 수 통계")
@RestController
@RequiredArgsConstructor
public class VisitCountController {

    private final VisitService visitService;
    private final CookieUtils cookieUtils;


    @Operation(summary = "최초 방문시 쿠키 발급",
            description = "최초 방문 시 해당 API 요청을 보내면 DB에 저장한 후 쿠키를 반환합니다. 쿠키의 만료 시간은 당일 23시 59분까지이며, 쿠키가 있으면 해당 API 요청을 보내지 않아도 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                examples = @ExampleObject(value = "{\"success\":true,\"message\" : \"방문 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD Request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"현재 조회된 IP주소 입니다.\"}"))),
    })
    @PostMapping("/api/visit")
    public ResponseEntity<?> ipCount(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        // 오늘 자정 시간 (UTC)
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
        ZonedDateTime midnightUTC = midnight.atZone(ZoneId.of("UTC"));
        // 현재 시간부터 자정까지의 초 단위 차이 계산
        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnightUTC);

        ResponseCookie responseCookie = cookieUtils.createCookie("visitors", UUID.randomUUID().toString(), (int) secondsUntilMidnight);
        LocalDateTime expireAdAt = now.toLocalDate().atStartOfDay().plusDays(1).minusSeconds(5);

        boolean ipTracked = isIpTracked(request, ipAddress, expireAdAt);
        if(!ipTracked){
            visitService.save(ipAddress,expireAdAt);
        }

        log.info("============== 현재 Client ip=============={}",ipAddress);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(new ControllerApiResponse<>(true,"방문 성공"));
    }

    @Operation(summary = "당일 방문자 수 조회",description = "당일 방문자 수를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\" : \"성공\",\"data\" : \"12\"}")))
    })
    @GetMapping("/api/admin/visit-count/today")
    public ResponseEntity<?> todayCount(){
        int currentVisitCount = visitService.getCurrentVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"성공",currentVisitCount));
    }

    @Operation(summary = "전일 방문자수 조회",description = "전일 방문자 수를 조회하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\" : \"성공\",\"data\" : \"142\"}")))
    })
    @GetMapping("/api/admin/visit-count/before")
    public ResponseEntity<?> before(){
        int previousVisitCount = visitService.getPreviousVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"성공",previousVisitCount));
    }

    @Operation(summary = "전체 방문자수 조회",description = "현재 방문자 수를 모두 조회 하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\" : \"성공\",\"data\" : \"14902\"}")))
    })
    @GetMapping("/api/admin/visit-count/all")
    public ResponseEntity<?> allCount(){
        int totalVisitCount = visitService.getTotalVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"성공",totalVisitCount));
    }

    @Operation(summary = "일간 방문자수 조회",description = "days를 보내지 않으면 한 달간의 사용자 방문자 수를 조회합니다. days를 true로 보내면 14일간의 방문자 수를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"일간 방문자수 조회\",\"data\":[{\"date\":\"2024-06-06\",\"count\":4},{\"date\":\"2024-06-06\",\"count\":23}]}")))
    })
    @GetMapping("/api/admin/visit-count/days")
    public ResponseEntity<?> days(@RequestParam(name = "days",required = false) Boolean days){

        List<DayDto> dailyVisitCount = visitService.getDailyVisitCount(days);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"일간 방문자수 조회",dailyVisitCount));
    }
    
    @Operation(summary = "주간 방문자수 조회",description = "1주간의 방문자 수를 총 10주간 조회합니다. 기준은 일요일부터 토요일까지이며, 현재 기준으로 주를 계산하여 5월 1~4일이 1주차로 계산됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"주간  방문자수 조회\",\"data\":[{\"week\":\"2024-05-12\",\"count\":28156},{\"week\":\"2024-05-05\",\"count\":36076}]}")))
    })
    @GetMapping("/api/admin/visit-count/week")
    public ResponseEntity<?> week(){
        List<WeekDto> weeklyVisitCount = visitService.getWeeklyVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"주간 방문자수 조회",weeklyVisitCount));
    }

    @Operation(summary = "월간 방문자수 조회",description = "1주간의 방문자 수를 총 10달간 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"월간 방문자수 조회\",\"data\":[{\"month\":\"2024-06-01\",\"count\":4},{\"month\":\"2024-05-01\",\"count\":23}]}")))
    })
    @GetMapping("/api/admin/visit-count/month")
    public ResponseEntity<?> month(){
        List<MonthDto> monthlyVisitCount = visitService.getMonthlyVisitCount();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"월간 방문자수 조회",monthlyVisitCount));
    }

    private  String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    private boolean isIpTracked(HttpServletRequest request, String ipAddress, LocalDateTime expireAt) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("visitors".equals(cookie.getName())) {
                    return visitService.ipExists(ipAddress);
                }
            }
        }
        return false;
    }
}
