package com.team.RecipeRadar.domain.notification.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.notification.domain.Notification;
import com.team.RecipeRadar.domain.notification.dto.NotificationDto;
import com.team.RecipeRadar.domain.notification.dto.response.MainNotificationResponse;
import com.team.RecipeRadar.domain.notification.dto.response.UserInfoNotificationResponse;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityTestConfig.class)
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean NotificationService notificationService;
    @Test
    @DisplayName("SSE 연결 테스트")
    @CustomMockUser
    void connect() throws Exception {
        SseEmitter mockEmitter = mock(SseEmitter.class);
        when(notificationService.subscribe(anyLong(), isNull())).thenReturn(mockEmitter);

        mockMvc.perform(get("/api/user/connect")
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isOk());

        String eventData = "Connect SSE";
        mockEmitter.send(eventData);  // 이벤트 전송

        verify(mockEmitter).send(eventData);
    }

    @Test
    @DisplayName("사용자 알림 페지이 테스트")
    @CustomMockUser
    void userNotificationPage() throws Exception {

        UserInfoNotificationResponse userInfoNotificationResponse = new UserInfoNotificationResponse(true, List.of(new NotificationDto(), new NotificationDto()));
        when(notificationService.userInfoNotification(anyLong(),isNull(),any(Pageable.class))).thenReturn(userInfoNotificationResponse);

        mockMvc.perform(get("/api/user/info/notification"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2));
    }

    @Test
    @DisplayName("메인페이지 알림테스트(7개 조회)")
    @CustomMockUser
    void mainNotification() throws Exception {

        List<NotificationDto>  notificationDtoList = new ArrayList<>();
        for(int i =1; i<=7;i++){
            notificationDtoList.add(new NotificationDto((long)i,"테스트","url"));
        }

        MainNotificationResponse mainNotificationResponse = MainNotificationResponse.of(notificationDtoList);
        when(notificationService.mainNotification(anyLong())).thenReturn(mainNotificationResponse);

        mockMvc.perform(get("/api/user/main/notification"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.notification.size()").value(7));
    }
    
    @Test
    @DisplayName("알림 삭제 테스트")
    @CustomMockUser
    void deleteNotification() throws Exception {
        doNothing().when(notificationService).deleteAllNotification(anyList());

        mockMvc.perform(delete("/api/user/notification?notificationIds=1,2,4"))
                .andExpect(jsonPath("$.message").value("삭제 성공"));
    }

}