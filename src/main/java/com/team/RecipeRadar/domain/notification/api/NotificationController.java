package com.team.RecipeRadar.domain.notification.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.notification.dto.MainNotificationResponse;
import com.team.RecipeRadar.domain.notification.dto.ResponseUserInfoNotification;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    //Last-Event-ID는 SSE 연결이 끊어졌을 경우, 클라이언트가 수신한 마지막 데이터의 id 값을 의미합니다. 항상 존재하는 것이 아니기 때문에 false
    @GetMapping(value = "/connect",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(notificationService.subscribe(principalDetails.getMember().getId(), lastEventId));
    }

    @GetMapping("/info/notification")
    public ResponseEntity<?> notificationPage(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @RequestParam(value = "last-id",required = false) Long lasId, Pageable pageable){
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        ResponseUserInfoNotification responseUserInfoNotification = notificationService.userInfoNotification(memberDto.getId(), lasId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",responseUserInfoNotification));
    }

    @GetMapping("/main/notification")
    public ResponseEntity<?> notificationMainPage(@AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        MainNotificationResponse mainNotificationResponse = notificationService.mainNotification(memberDto.getId());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",mainNotificationResponse));
    }
}
