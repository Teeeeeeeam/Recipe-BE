package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.UserInfoResponse;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MemberServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtProvider jwtProvider;

    @InjectMocks MemberServiceImpl memberService;

    @Test
    @DisplayName("사용자 페이지의 개인 정보를 불러오는 테스트")
    void getMembers() {
        // 가짜 토큰과 가짜 로그인 ID 설정
        String loginId = "testId";
        String username = "이름";

        // 가짜 멤버 객체 생성
        Member member = Member.builder()
                .id(1L)
                .loginId(loginId)
                .username(username)
                .nickName("닉네임")
                .email("이메일")
                .login_type("normal").build();

        // 가짜 멤버 객체 반환 설정
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        // getMembers 메서드 호출 및 반환된 UserInfoResponse 객체 저장
        UserInfoResponse members = memberService.getMembers(loginId, username);

        // 검증
        assertThat(members.getUsername()).isEqualTo("이름");
        assertThat(members.getNickName()).isEqualTo("닉네임");
        assertThat(members.getEmail()).isEqualTo("이메일");
        assertThat(members.getLoginType()).isEqualTo("normal");
    }

    @Test
    @DisplayName("사용자 페이지의 다른 사용자 접근시 예외 발생 테스트")
    void getMembers_AccessDenied() {
        String loginId = "testId";
        String differentUsername= "differentUsername";       //다른 사용자 ID
        

        // 가짜 멤버 객체 생성
        Member member = Member.builder()
                .id(1L)
                .loginId(loginId)
                .username("이름")
                .nickName("닉네임")
                .email("이메일")
                .login_type("normal").build();

        // 가짜 멤버 객체 반환 설정
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        // AccessDeniedException 예외 발생
        assertThatThrownBy(() -> memberService.getMembers(loginId,differentUsername)).isInstanceOf(AccessDeniedException.class);

    }


}