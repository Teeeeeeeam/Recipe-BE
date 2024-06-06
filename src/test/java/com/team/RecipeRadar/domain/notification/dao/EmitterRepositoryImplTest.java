package com.team.RecipeRadar.domain.notification.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;


class EmitterRepositoryImplTest {

    private EmitterRepository emitterRepository;

    @BeforeEach
    void setUp() {
        emitterRepository = new EmitterRepositoryImpl();
    }

    @Test
    @DisplayName("새로운 Emitter 저장")
    void save() {
        String emitterId = "member1";
        SseEmitter sseEmitter = new SseEmitter();

        SseEmitter result = emitterRepository.save(emitterId, sseEmitter);

        assertThat(result).isEqualTo(sseEmitter);
        assertThat(emitterRepository.findAllEmitterStartWithByMemberId(emitterId)).containsKey(emitterId);
    }

    @Test
    @DisplayName("이벤트 캐시 저장")
    void saveEventCache() {
        String emitterId = "member1";
        Object event = new Object();

        emitterRepository.saveEventCache(emitterId, event);

        Map<String, Object> eventCache = emitterRepository.findAllEventCacheStartWithByMemberId(emitterId);
        assertThat(eventCache).containsKey(emitterId);
        assertThat(eventCache.get(emitterId)).isEqualTo(event);
    }

    @Test
    @DisplayName("회원 ID로 시작하는 모든 Emitter 찾기")
    void findAllEmitterStartWithByMemberId() {
        String memberId = "member1";
        SseEmitter sseEmitter1 = new SseEmitter();
        SseEmitter sseEmitter2 = new SseEmitter();
        emitterRepository.save(memberId + "1", sseEmitter1);
        emitterRepository.save(memberId + "2", sseEmitter2);

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);

        assertThat(emitters).hasSize(2);
        assertThat(emitters).containsKeys(memberId + "1", memberId + "2");
    }

    @Test
    @DisplayName("회원 ID로 시작하는 모든 이벤트 캐시 찾기")
    void findAllEventCacheStartWithByMemberId() {
        String memberId = "member1";
        Object event1 = new Object();
        Object event2 = new Object();
        emitterRepository.saveEventCache(memberId + "1", event1);
        emitterRepository.saveEventCache(memberId + "2", event2);

        Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(memberId);

        assertThat(events).hasSize(2);
        assertThat(events).containsKeys(memberId + "1", memberId + "2");
    }

    @Test
    @DisplayName("Emitter ID로 Emitter 삭제")
    void deleteById() {
        String emitterId = "member1";
        SseEmitter sseEmitter = new SseEmitter();
        emitterRepository.save(emitterId, sseEmitter);

        emitterRepository.deleteById(emitterId);

        assertThat(emitterRepository.findAllEmitterStartWithByMemberId(emitterId)).doesNotContainKey(emitterId);
    }

    @Test
    @DisplayName("회원 ID로 시작하는 모든 Emitter 삭제")
    void deleteAllEmitterStartWithId() {
        String memberId = "member1";
        SseEmitter sseEmitter1 = new SseEmitter();
        SseEmitter sseEmitter2 = new SseEmitter();
        emitterRepository.save(memberId + "1", sseEmitter1);
        emitterRepository.save(memberId + "2", sseEmitter2);

        emitterRepository.deleteAllEmitterStartWithId(memberId);

        assertThat(emitterRepository.findAllEmitterStartWithByMemberId(memberId)).isEmpty();
    }

    @Test
    @DisplayName("회원 ID로 시작하는 모든 이벤트 캐시 삭제")
    void deleteAllEventCacheStartWithId() {
        String memberId = "member1";
        Object event1 = new Object();
        Object event2 = new Object();
        emitterRepository.saveEventCache(memberId + "1", event1);
        emitterRepository.saveEventCache(memberId + "2", event2);

        emitterRepository.deleteAllEventCacheStartWithId(memberId);

        assertThat(emitterRepository.findAllEventCacheStartWithByMemberId(memberId)).isEmpty();
    }
}