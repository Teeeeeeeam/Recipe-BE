package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.application.RecipeLikeServiceImpl;
import com.team.RecipeRadar.domain.like.dao.RecipeLikeRepository;
import com.team.RecipeRadar.domain.like.dto.RecipeLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class RecipeLikeServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock RecipeLikeRepository recipeLikeRepository;
    @Mock RecipeRepository recipeRepository;
    @Mock JwtProvider jwtProvider;

    @InjectMocks
    RecipeLikeServiceImpl recipeLikeService;

    @Test
    @DisplayName("레시피 좋아요")
    void add_recipe_like(){
        Member member = Member.builder().id(2l).loginId("testId").build();
        Recipe recipe = Recipe.builder().id(3l).content("content").likeCount(0).build();

        RecipeLikeDto build = RecipeLikeDto.builder().memberId(2l).recipeId(3l).build();


        when(recipeLikeRepository.existsByMemberIdAndRecipeId(build.getMemberId(),build.getRecipeId())).thenReturn(false);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));


        Boolean aBoolean = recipeLikeService.addLike(build);

        assertThat(aBoolean).isFalse();
        assertThat(recipe.getLikeCount()).isEqualTo(1);

        verify(recipeLikeRepository, times(0)).deleteByMemberIdAndRecipeId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("레시피 좋아요되어있을때")
    void add_recipe_like_delete(){
        Member.builder().id(2l).loginId("testId").build();
        Recipe recipe = Recipe.builder().id(3l).content("content").likeCount(0).build();

        RecipeLikeDto build = RecipeLikeDto.builder().memberId(2l).recipeId(3l).build();

        when(recipeLikeRepository.existsByMemberIdAndRecipeId(build.getMemberId(),build.getRecipeId())).thenReturn(true);
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));

        Boolean aBoolean = recipeLikeService.addLike(build);

        assertThat(aBoolean).isTrue();
        verify(recipeLikeRepository, times(1)).deleteByMemberIdAndRecipeId(anyLong(), anyLong());
    }


    @Test
    @DisplayName("jwt토큰을 이용한 좋아요 되어있는지 테스트")
    public void testCheckLike() {
        // 가짜 JWT 토큰 및 기타 필요한 데이터 설정
        String fakeJwtToken = "fakeToken";
        Long id = 1l;

        Member member = Member.builder().id(2l).loginId("fakeLoginId").build();
        // jwtProvider 메서드 호출에 대한 목 설정
        when(jwtProvider.validateAccessToken(fakeJwtToken)).thenReturn("fakeLoginId");

        // memberRepository 메서드 호출에 대한 목 설정
        when(memberRepository.findByLoginId("fakeLoginId")).thenReturn(member);

        // postLikeRepository 메서드 호출에 대한 목 설정
        when(recipeLikeRepository.existsByMemberIdAndRecipeId(anyLong(), anyLong())).thenReturn(true);

        // 메서드 호출 및 결과 확인
        assertTrue(recipeLikeService.checkLike(fakeJwtToken, id));
    }

    @Test
    @DisplayName("jwt토큰을 이용한 좋아요 되어있는지 테스트")
    public void test_Check_UnLike() {
        // 가짜 JWT 토큰 및 기타 필요한 데이터 설정
        String fakeJwtToken = "fakeToken";
        Long id = 1l;

        Member member = Member.builder().id(2l).loginId("fakeLoginId").build();
        // jwtProvider 메서드 호출에 대한 목 설정
        when(jwtProvider.validateAccessToken(fakeJwtToken)).thenReturn("fakeLoginId");

        // memberRepository 메서드 호출에 대한 목 설정
        when(memberRepository.findByLoginId("fakeLoginId")).thenReturn(member);

        // postLikeRepository 메서드 호출에 대한 목 설정
        when(recipeLikeRepository.existsByMemberIdAndRecipeId(anyLong(), anyLong())).thenReturn(false);

        // 메서드 호출 및 결과 확인
        assertFalse(recipeLikeService.checkLike(fakeJwtToken, id));
    }
}