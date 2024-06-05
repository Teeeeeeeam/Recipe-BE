package com.team.RecipeRadar.domain.notification.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.notification.dto.MainNotificationResponse;
import com.team.RecipeRadar.domain.notification.dto.ResponseUserInfoNotification;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@Tag(name = "알림 컨트롤러" ,description = "실시간 알림 컨트롤러")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    //Last-Event-ID는 SSE 연결이 끊어졌을 경우, 클라이언트가 수신한 마지막 데이터의 id 값을 의미합니다. 항상 존재하는 것이 아니기 때문에 false
    @Operation(summary = "서버와 연결",
            description = "Header에 Accept - text/event-stream 로 요청후 재요청 시간은 1시간")
    @GetMapping(value = "/connect",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(notificationService.subscribe(principalDetails.getMember().getId(), lastEventId));
    }

    @Operation(summary = "사용자 알림 목록", description = "사용자에 대한 알림을 모두 볼수있는 API")
    @GetMapping("/info/notification")
    public ResponseEntity<?> notificationPage(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @RequestParam(value = "last-id",required = false) Long lasId, Pageable pageable){
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        ResponseUserInfoNotification responseUserInfoNotification = notificationService.userInfoNotification(memberDto.getId(), lasId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",responseUserInfoNotification));
    }

    @Operation(summary = "메인페이지 알림 목록", description = "메인페이지의 알림 목록을 7개 표시")
    @GetMapping("/main/notification")
    public ResponseEntity<?> notificationMainPage(@AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        MainNotificationResponse mainNotificationResponse = notificationService.mainNotification(memberDto.getId());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",mainNotificationResponse));
    }
}
