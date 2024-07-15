package com.team.RecipeRadar.domain.notification.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.dao.EmitterRepository;
import com.team.RecipeRadar.domain.notification.dao.NotificationRepository;
import com.team.RecipeRadar.domain.notification.domain.Notification;
import com.team.RecipeRadar.domain.notification.domain.NotificationType;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock EmitterRepository  emitterRepository;
    @Mock NotificationRepository notificationRepository;
    @Mock MemberRepository memberRepository;

    @InjectMocks NotificationService notificationService;

    private Member member;

    @BeforeEach
    void setUp(){
        member = Member.builder().id(1l).nickName("닉네임").build();
    }
    @Test
    @DisplayName("subscribe lastEventId 가들어오지않았을때 테스트 ")
    void subscribeTest() throws IOException {
        SseEmitter mockEmitter = mock(SseEmitter.class);
        when(emitterRepository.save(anyString(), any(SseEmitter.class))).thenReturn(mockEmitter);

        SseEmitter emitter = notificationService.subscribe(1l, "");

        verify(emitterRepository, times(1)).save(anyString(), any(SseEmitter.class));
        verify(emitter, times(1)).send(any());       //클라이언트에게 1회 메세지 전송
        verify(emitterRepository, times(0)).deleteById(anyString()); //시간초과나 비동기 일어나지 않았기 떄문에 실행 x
        verify(emitterRepository, times(0)).deleteAllEmitterStartWithEmitterId(anyString());
        verify(emitterRepository, times(0)).deleteAllEventCacheStartWithId(anyString());
        verify(emitterRepository, times(0)).findAllEventCacheStartWithByMemberId(anyString());
    }

    @Test
    @DisplayName("subscribe lastEventId 들어왔을때 테스트 ")
    void subscribeLastEventIdTest() throws IOException {
        SseEmitter mockEmitter = mock(SseEmitter.class);
        when(emitterRepository.save(anyString(), any(SseEmitter.class))).thenReturn(mockEmitter);

        SseEmitter emitter = notificationService.subscribe(1l, "lastId");

        verify(emitterRepository, times(1)).save(anyString(), any(SseEmitter.class));
        verify(emitter, times(1)).send(any());       //클라이언트에게 1회 메세지 전송
        verify(emitterRepository, times(0)).deleteById(anyString()); //시간초과나 비동기 일어나지 않았기 떄문에 실행 x
        verify(emitterRepository, times(1)).deleteAllEmitterStartWithEmitterId(anyString());        //lastEventId가 존재하기때문에 한번 실행
        verify(emitterRepository, times(1)).deleteAllEventCacheStartWithId(anyString());
        verify(emitterRepository, times(1)).findAllEventCacheStartWithByMemberId(anyString());
    }
    @Test
    @DisplayName("SSE 이메일 전송 테스트")
    void sendTest() {
        Notification notification = Notification.builder()
                .content("알림")
                .receiver(member)
                .toName("닉네임")
                .notificationType(NotificationType.POSTLIKE)
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(emitterRepository.findAllEmitterStartWithByMemberId(anyString())).thenReturn(Collections.singletonMap("1", new SseEmitter()));
        
        notificationService.send(member, NotificationType.POSTLIKE, "알림발생!", "test/url", "닉네임");
        
        verify(emitterRepository, times(1)).saveEventCache(eq("1"), eq(notification));      // 알림전송 한번 실행
    }
    
    @Test
    @DisplayName("좋아요 알림 삭제 테스트")
    void deleteLike(){
        doNothing().when(notificationRepository).deletePostLike(anyLong(),anyLong(),anyLong());
        notificationService.deleteLikeNotification(1l,2l,3l);
        verify(notificationRepository, times(1)).deletePostLike(anyLong(),anyLong(),anyLong());
    }
    
    @Test
    @DisplayName("댓글 알림 삭제 테스트")
    void deleteComment(){
        doNothing().when(notificationRepository).deleteComment(anyLong(),anyLong(),anyLong());
        notificationService.deleteCommentNotification(1l,2l,3l);
        verify(notificationRepository, times(1)).deleteComment(anyLong(),anyLong(),anyLong());
    }

    @Test
    @DisplayName("알림 내역 일괄 삭제  테스트")
    void deleteNotification(){
        when(notificationRepository.findAllById(anyList())).thenReturn(
                List.of(
                        Notification.builder().build(),
                        Notification.builder().build()
                        ));
        notificationService.deleteAllNotification(List.of(1l,2l));
        verify(notificationRepository, times(2)).delete(any(Notification.class));
    }


    @Test
    @DisplayName("댓글 알림")
    void sendCommentNotificationTest() {
        // Given
        Post post = Post.builder().id(1L).member(Member.builder().id(1L).nickName("닉네임1").build()).build();

        // When
        notificationService.sendCommentNotification(post, "닉네임");

        // Then
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(emitterRepository, times(1)).findAllEmitterStartWithByMemberId(anyString());
    }

    @Test
    @DisplayName("좋아요시 알림 테스트")
    void sendPostLikeNotificationTest() {
        Post post = Post.builder().id(1L).member(Member.builder().id(1L).nickName("닉네임1").build()).build();

        notificationService.sendPostLikeNotification(post, "닉네임");

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(emitterRepository, times(1)).findAllEmitterStartWithByMemberId(anyString());
    }

    @Test
    @DisplayName("답변알림 테스트")
    void completeQuestionTest() {
        Question question = Question.builder().id(1L).member(Member.builder().id(1L).build()).title("질문").build();
        
        notificationService.completeQuestion(question, "어드민");
        
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(emitterRepository, times(1)).findAllEmitterStartWithByMemberId(anyString());
    }

    @Test
    @DisplayName("어드민에게 문의사항 등록시 알림 테스트")
    void sendAdminNotificationTest() {
        Question question = Question.builder().id(1L).member(Member.builder().id(1L).build()).questionType(QuestionType.ACCOUNT_INQUIRY).build();
        when(memberRepository.adminMember()).thenReturn(Collections.singletonList(Member.builder().id(2L).build()));

        notificationService.sendAdminNotification(question, "어드민");

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(emitterRepository, times(1)).findAllEmitterStartWithByMemberId(anyString());
    }
    
}