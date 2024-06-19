package com.team.RecipeRadar.global.auth.application;

import com.team.RecipeRadar.domain.account.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.account.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.auth.dto.response.MemberInfoResponse;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock MemberRepository memberRepository;
    @Mock AccountRetrievalRepository accountRetrievalRepository;
    @Mock JwtProvider jwtProvider;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthServiceImpl authService;

    private static Long memberId = 1l;
    @Test
    @DisplayName("엑세스 토큰을 통해 변환 테스트")
    void getAccessToken_with_info(){
        String fakeToken = "fakeToken";
        String loginId = "loginId";
        Member member = Member.builder()
                .id(1L)
                .username("사용자이름")
                .loginId(loginId)
                .nickName("닉네임")
                .login_type("normal")
                .build();

        when(jwtProvider.validateAccessToken(eq(fakeToken))).thenReturn(loginId);
        when(memberRepository.findByLoginId(eq(loginId))).thenReturn(member);

        MemberInfoResponse info = authService.accessTokenMemberInfo(fakeToken);

        assertThat(info.getLoginId()).isEqualTo(loginId);
        assertThat(info.getLoginType()).isEqualTo(member.getLogin_type());
    }

    @Test
    @DisplayName("사용자 페이지 접근시 비밀번호가 일치하면 토큰 생성")
    void cookie_Token_Access_PasswordMatch() {
        String loginId = "testId";
        Member member = Member.builder()
                .id(memberId)
                .username("username")
                .nickName("nickName")
                .password("1234")
                .loginId(loginId)
                .email("test@email.com")
                .login_type("normal")
                .build();

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        boolean machPassword = true; // 비밀번호 일치하는 상황
        when(passwordEncoder.matches("1234", member.getPassword())).thenReturn(machPassword);

        String uuidId = UUID.randomUUID().toString();
        LocalDateTime plusMinutes = LocalDateTime.now().plusMinutes(20);
        AccountRetrieval accountRetrieval = AccountRetrieval.builder()
                .verificationId(uuidId)
                .loginId(loginId)
                .expireAt(plusMinutes)
                .build();
        when(accountRetrievalRepository.save(any())).thenReturn(accountRetrieval);

        String token = authService.userToken(memberId,"1234");

        assertThat(token).isEqualTo(uuidId);
    }


    @Test
    @DisplayName("사용자 페이지 접근시 비밀번호가 일치하지않아 예외 발생")
    void cookie_Token_Access_PasswordMatch_Fail() {
        String loginId = "testId";
        Member member = Member.builder()
                .id(memberId)
                .username("username")
                .nickName("nickName")
                .password("1234")
                .loginId(loginId)
                .email("test@email.com")
                .login_type("normal")
                .build();

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        boolean machPassword = false; // 비밀번호 일치하는 상황
        when(passwordEncoder.matches("1111", member.getPassword())).thenReturn(machPassword);

        assertThatThrownBy(() -> authService.userToken(memberId,"1111")).isInstanceOf(InvalidIdException.class);
    }
}