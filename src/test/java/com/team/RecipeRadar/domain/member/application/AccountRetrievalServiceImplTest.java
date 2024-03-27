package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.email.application.JoinEmailServiceImplV1;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AccountRetrievalServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock JoinEmailServiceImplV1 joinEmailServiceImplV1;

    @InjectMocks AccountRetrievalServiceImpl accountRetrievalService;


    @Test
    @DisplayName("아이디 찾기시에 일반,소셜 로그인 존재시")
    void findLoginId() {
        String email = "test@email.com";
        String username = "username";

        Member normal = Member.builder().id(1L).login_type("normal").loginId("testId").email(email).username(username).build();     //일반사용자
        Member social = Member.builder().id(2L).login_type("kakao").loginId("testId").email(email).username(username).build();      // 소셜 로그인 사용자

        List<Member> list = new ArrayList<>();
        list.add(normal);
        list.add(social);

        when(memberRepository.findByUsernameAndEmail(eq(username), eq(email))).thenReturn(list);        //리스트로 반환

        String realCode = "code";
        when(joinEmailServiceImplV1.getCode()).thenReturn(realCode);        // 이메일인증코드

        List<Map<String, String>> loginId = accountRetrievalService.findLoginId(username, email, realCode);     //반환

        assertThat(loginId.get(0).get("로그인 타입")).isEqualTo("normal");
        assertThat(loginId.get(1).get("로그인 타입")).isEqualTo("kakao");
        assertThat(loginId.size()).isEqualTo(2);
    }


    @Test
    @DisplayName("아이디 찾기시에 인증번호 틀리는 테스트")
    void emailCode_fail() {
        String email = "test@email.com";
        String username = "username";

        Member normal = Member.builder().id(1L).login_type("normal").loginId("testId").email(email).username(username).build();     //일반사용자
        Member social = Member.builder().id(2L).login_type("kakao").loginId("testId").email(email).username(username).build();      // 소셜 로그인 사용자

        List<Member> list = new ArrayList<>();
        list.add(normal);
        list.add(social);

        when(memberRepository.findByUsernameAndEmail(eq(username), eq(email))).thenReturn(list);        //리스트로 반환


        String realCode = "code";
        String fakeCode = "fake";
        when(joinEmailServiceImplV1.getCode()).thenReturn(realCode);        // 이메일인증코드

        List<Map<String, String>> loginId = accountRetrievalService.findLoginId(username, email, fakeCode);     //반환

        assertThat(loginId.get(0).get("인증 번호")).isEqualTo("인증번호가 일치하지 않습니다.");
        assertThat(loginId.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 정보가 없을때")
    void find_fail_read_member_info() {
        String email = "test@email.com";
        String username = "username";

        Member normal = Member.builder().id(1L).login_type("normal").loginId("testId").email(email).username(username).build();     //일반사용자
        Member social = Member.builder().id(2L).login_type("kakao").loginId("testId").email(email).username(username).build();      // 소셜 로그인 사용자

        List<Member> list = new ArrayList<>();
        list.add(normal);
        list.add(social);

        when(memberRepository.findByUsernameAndEmail(eq("등록되지 않은 사용자"), eq(email))).thenReturn(Collections.emptyList());

        String realCode = "code";
        when(joinEmailServiceImplV1.getCode()).thenReturn(realCode);        // 이메일인증코드

        List<Map<String, String>> loginId = accountRetrievalService.findLoginId("등록되지 않은 사용자", email, realCode);     //반환
        assertThat(loginId.get(0).get("가입 정보")).isEqualTo("해당 정보로 가입된 회원은 없습니다.");
        assertThat(loginId.size()).isEqualTo(1);
    }
}