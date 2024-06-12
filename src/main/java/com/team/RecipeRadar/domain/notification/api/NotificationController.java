package com.team.RecipeRadar.domain.notification.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.notification.dto.MainNotificationResponse;
import com.team.RecipeRadar.domain.notification.dto.ResponseUserInfoNotification;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


@RestController
@Tag(name = "공용 - 알림 컨트롤러" ,description = "실시간 알림 컨트롤러")
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

    @Operation(summary = "사용자 알림 내역(페이징)", description = "사용자에 대한 알림의 무한 페이징 입니다.",tags = "사용자 - 마이페이지 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value =   "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"hasNext\":false,\"notification\":[{\"id\":274,\"url\":\"/api/user/question/6\"},{\"id\":272,\"url\":\"/api/user/question/11\"},{\"id\":260,\"url\":\"/api/user/question/5\"}]}}")))
    })
    @GetMapping("/info/notification")
    public ResponseEntity<?> notificationPage(@Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails,
                                              @RequestParam(value = "last-id",required = false) Long lasId,
                                              @Parameter(example = "{\"size\":10}")Pageable pageable){
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        ResponseUserInfoNotification responseUserInfoNotification = notificationService.userInfoNotification(memberDto.getId(), lasId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",responseUserInfoNotification));
    }

    @Operation(summary = "메인페이지 알림 목록", description = "메인페이지의 알림 목록을 7개 표시(일반 사용자, 어드민 사용자 같이 해당 API 사용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value =  "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"notification\":[{\"id\":1,\"url\":\"/api/user/question/1\"},{\"id\":2,\"url\":\"/api/user/question/2\"}]}}")))
    })
    @GetMapping("/main/notification")
    public ResponseEntity<?> notificationMainPage(@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        MainNotificationResponse mainNotificationResponse = notificationService.mainNotification(memberDto.getId());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",mainNotificationResponse));
    }

    @Operation(summary = "알림 삭제", description = "메인 페이지 및 알림 전제조회 페이지에서 공통으로 사용가능(단일,일괄 삭제가능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false , \"message\" : \"해당 알림을 찾을수 없습니다.\"}")))
    })
    @DeleteMapping("/user/notification")
    public ResponseEntity<?> deleteNotification(@RequestParam("ids")List<Long> ids){
        notificationService.deleteAllNotification(ids);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"삭제 성공"));
    }
}
