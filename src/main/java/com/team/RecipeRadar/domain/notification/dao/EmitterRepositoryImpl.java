package com.team.RecipeRadar.domain.notification.dao;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository{

    // SSE 이밋터를 저장하기 위한 맵
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 이벤트를 캐시하기 위한 맵
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    /**
     * 주어진 이밋터 ID와 SSE 이밋터를 저장하는 메서드
     *
     * @param emitterId  SSE 이밋터의 ID
     * @param sseEmitter SSE 이밋터 객체
     * @return 저장된 SSE 이밋터 객체
     */
    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId,sseEmitter);
        return sseEmitter;
    }

    /**
     * 주어진 이밋터 ID에 관련된 이벤트를 캐시를 찾는 메서드
     *
     * @param emitterId 이밋터 ID
     * @param event     캐시할 이벤트 객체
     */
    @Override
    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId,event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String emitterId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(emitterId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    /**
     * 주어진 멤버 ID 접두사로 시작하는 모든 이벤트 캐시를 찾는 메서드.
     *
     * @param memberId 멤버 ID 접두사
     * @return 해당 접두사로 시작하는 모든 이벤트 캐시의 맵
     */
    @Override
    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    /**
     * 주어진 이밋터 ID에 해당하는 SSE 이밋터를 삭제하는 메서드.
     */
    @Override
    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

    /**
     * 주어진 이밋터 ID 접두사로 시작하는 모든 SSE 이밋터를 삭제하는 메서드
     */
    @Override
    public void deleteAllEmitterStartWithEmitterId(String emitterId) {
        emitters.forEach(
                (key,emitter) -> {
                    if(key.startsWith(emitterId)){
                        emitters.remove(key);
                    }
                }
        );
    }

    /**
     * 주어진 멤버 ID 접두사로 시작하는 모든 이벤트 캐시를 삭제하는 메서드
     */
    @Override
    public void deleteAllEventCacheStartWithId(String memberId) {
        eventCache.forEach(
                (key,value) -> {
                    if(key.startsWith(memberId)){
                        eventCache.remove(key);
                    }
                }
        );
    }
}
