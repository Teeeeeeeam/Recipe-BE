package com.team.RecipeRadar.global.auth.dao;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.auth.domain.RefreshToken;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE)
class RefreshTokenRepositoryTest {

    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("리프레쉬 토큰에서 사용자 정보 조회")
    void exitsMember(){
        String loginId = "loginId";
        String refreshToken = "refreshToken";
        Member member = Member.builder().loginId(loginId).nickName("닉네임").build();
        Member saveMember = memberRepository.save(member);

        RefreshToken refreshToken1 = RefreshToken.createRefreshToken(saveMember, refreshToken, LocalDateTime.now().plusMinutes(10));
        refreshTokenRepository.save(refreshToken1);

        MemberDto memberDto = refreshTokenRepository.existsByRefreshTokenAndMember(refreshToken, member.getLoginId());
        assertThat(memberDto.getNickname()).isEqualTo(member.getNickName());
    }

    @Test
    @DisplayName("리프레쉬 토큰에서 사용자 정보 조회시 예외")
    void exitsMember_throw(){
        String loginId = "loginId";
        String refreshToken = "refreshToken";
        Member member = Member.builder().loginId(loginId).nickName("닉네임").build();
        Member saveMember = memberRepository.save(member);

        RefreshToken refreshToken1 = RefreshToken.createRefreshToken(saveMember, refreshToken, LocalDateTime.now().plusMinutes(10));
        refreshTokenRepository.save(refreshToken1);

        assertThatThrownBy(() -> refreshTokenRepository.existsByRefreshTokenAndMember("no",loginId)).isInstanceOf(NoSuchDataException.class);
    }
}