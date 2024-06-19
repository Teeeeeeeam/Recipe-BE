package com.team.RecipeRadar.global.jwt.Service;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.auth.application.AuthServiceImpl;
import com.team.RecipeRadar.global.auth.dto.response.MemberInfoResponse;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JwtAuthServiceImplTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock MemberRepository memberRepository;
    @Mock JwtProvider jwtProvider;
    @Mock AuthenticationManager authenticationManager;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks
    AuthServiceImpl jwtAuthService;

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

        MemberInfoResponse info = jwtAuthService.accessTokenMemberInfo(fakeToken);

        assertThat(info.getLoginId()).isEqualTo(loginId);
        assertThat(info.getLoginType()).isEqualTo(member.getLogin_type());
    }
}