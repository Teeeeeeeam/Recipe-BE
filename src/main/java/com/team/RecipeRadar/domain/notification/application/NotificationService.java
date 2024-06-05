package com.team.RecipeRadar.domain.notification.application;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.dao.EmitterRepository;
import com.team.RecipeRadar.domain.notification.dao.NotificationRepository;
import com.team.RecipeRadar.domain.notification.domain.Notification;
import com.team.RecipeRadar.domain.notification.domain.NotificationType;
import com.team.RecipeRadar.domain.notification.dto.MainNotificationResponse;
import com.team.RecipeRadar.domain.notification.dto.NotificationDto;
import com.team.RecipeRadar.domain.notification.dto.ResponseNotification;
import com.team.RecipeRadar.domain.notification.dto.ResponseUserInfoNotification;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    //연결 지속시간 한시간
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter subscribe(Long memberId,String lastEventId){
        // 고유한 아이디 생성
        String emitterId = memberId +"_"+System.currentTimeMillis();
        log.info("emitterId={}",emitterId);

        SseEmitter emitter  = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        //시간 초과나 비동기 요청이 안되면 자동으로 삭제 
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        //최초 연결시 더미데이터가 없으면 503 오류가 발생하기 때문에 해당 더미 데이터 생성
        sendToClient(emitter,emitterId, "EventStream Created. [memberId=" + memberId + "]");

        //lastEventId 있다는것은 연결이 종료됬다. 그래서 해당 데이터가 남아있는지 살펴보고 있다면 남은 데이터를 전송
        if(!lastEventId.isEmpty()){
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey())<0)
                    .forEach(entry -> sendToClient(emitter,entry.getKey(),entry.getValue()));
        }
        return emitter;

    }

    public void send(Member receiver, NotificationType notificationType, String content, String url,String toName) {
        Notification notification = notificationRepository.save(createNotification(receiver, notificationType, content, url,toName));
        String memberId = String.valueOf(receiver.getId());

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, new ControllerApiResponse<>(true,"댓글 알림", ResponseNotification.from(notification)));
                }
        );
    }

    public ResponseUserInfoNotification userInfoNotification(Long memberId, Long lastId, Pageable pageable){
        Slice<NotificationDto> notificationedPage = notificationRepository.notificationPage(memberId, pageable, lastId);
        return new ResponseUserInfoNotification(notificationedPage.hasNext(),notificationedPage.getContent());
    }

    public MainNotificationResponse mainNotification(Long memberId){
        return MainNotificationResponse.of(notificationRepository.notificationLimit(memberId));
    }
    //댓글 알림 삭제
    public void deleteLikeNotification(Long toId, Long fromId,Long postId){
        notificationRepository.deletePostLike(toId,fromId,postId);
    }

    //댓글 알림 삭제
    public void deleteCommentNotification(Long toId, Long fromId,Long commentId){
        notificationRepository.deleteComment(toId,fromId,commentId);
    }


    // 댓글 등록시 보내지는 알람
    public void sendCommentNotification(Post post, String nickName) {
        Member postAuthor = post.getMember();
        String content = nickName+"님이 댓글이 달렸습니다";
        String url = "/api/user/posts/" + post.getId();

        send(postAuthor, NotificationType.COMMENT, content, url,nickName);
    }

    // 레시피 좋아요시 보내지는 알람
    public void sendPostLikeNotification(Post post,String nickName) {
        Member postAuthor = post.getMember();
        String content = nickName+"님이 회원님의 게시글을 좋아합니다.";
        String url = "/api/user/posts/" + post.getId();

        send(postAuthor, NotificationType.POSTLIKE, content, url,nickName);
    }


    private Notification createNotification(Member receiver, NotificationType notificationType, String content, String url,String toName) {
        return  Notification.builder().receiver(receiver).notificationType(notificationType).content(content).url(url).toName(toName).build();
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new BadRequestException("배드 리퀘스트");
        }
    }

}
