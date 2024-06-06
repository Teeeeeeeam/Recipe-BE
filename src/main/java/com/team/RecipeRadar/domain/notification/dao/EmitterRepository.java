package com.team.RecipeRadar.domain.notification.dao;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    void saveEventCache(String emitterId, Object event);

    Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId); // 해당 member와 관련된 모든 emitter 찾는다.
    Map<String,Object> findAllEventCacheStartWithByMemberId(String memberId);   // 해당 member와 관련된 모든 event를 찾습니다.


    void deleteById(String emitterId);
    void deleteAllEmitterStartWithId(String memberId);
    void deleteAllEventCacheStartWithId(String memberId);
}
