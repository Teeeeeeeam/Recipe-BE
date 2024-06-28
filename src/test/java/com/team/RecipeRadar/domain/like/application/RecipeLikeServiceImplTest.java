package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dao.like.RecipeLikeRepository;
import com.team.RecipeRadar.domain.like.dto.request.RecipeLikeRequest;
import com.team.RecipeRadar.domain.like.dto.response.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeLikeServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock RecipeLikeRepository recipeLikeRepository;
    @Mock RecipeRepository recipeRepository;

    @InjectMocks
    RecipeLikeServiceImpl recipeLikeService;

    private Member member;
    private Recipe recipe;
    @BeforeEach
    void setUp(){
        member =  Member.builder().id(1l).loginId("testId").username("testuserName").build();
        recipe = Recipe.builder().id(3l).title("title").cookingLevel("1").likeCount(10).build();

    }
    @Test
    @DisplayName("레시피 좋아요")
    void add_recipe_like(){
        RecipeLikeRequest build = RecipeLikeRequest.builder().recipeId(recipe.getId()).build();

        when(recipeLikeRepository.existsByMemberIdAndRecipeId(member.getId(),build.getRecipeId())).thenReturn(false);      // 좋아요가 되어있지않음
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));

        Boolean aBoolean = recipeLikeService.addLike(build,member.getId());

        assertThat(aBoolean).isFalse();
        assertThat(recipe.getLikeCount()).isEqualTo(11);         //결국은 0 에서 하나증간된 1로 변경

        verify(recipeLikeRepository, times(0)).deleteByMemberIdAndRecipeId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("레시피 좋아요되어있을때")
    void add_recipe_like_delete(){
        RecipeLikeRequest recipeLikeRequest = RecipeLikeRequest.builder().recipeId(recipe.getId()).build();

        when(recipeLikeRepository.existsByMemberIdAndRecipeId(member.getId(),recipe.getId())).thenReturn(true);
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        Boolean aBoolean = recipeLikeService.addLike(recipeLikeRequest,member.getId());

        assertThat(aBoolean).isTrue();
        assertThat(recipe.getLikeCount()).isEqualTo(9);
        verify(recipeLikeRepository, times(1)).deleteByMemberIdAndRecipeId(anyLong(), anyLong());
    }


    @Test
    @DisplayName("좋아요 되어있는지 테스트")
    public void testCheckLike() {
        when(recipeLikeRepository.existsByMemberIdAndRecipeId(anyLong(), anyLong())).thenReturn(true);

        assertTrue(recipeLikeService.checkLike(member.getId(), recipe.getId()));
    }

    @Test
    @DisplayName("정상적으로 회원의 좋아요 정보를 페이지별로 가져오는지 확인")
    void Test_Get_User_LikesByPage() {
        // 테스트에 필요한 가짜 데이터 생성
        List<UserLikeDto> userLikeDtos = new ArrayList<>();
        userLikeDtos.add(new UserLikeDto(1L, 1l,"Content 1", "Title 1"));
        userLikeDtos.add(new UserLikeDto(2L, 1l,"Content 2", "Title 2"));

        Slice<UserLikeDto> userDtoSlice = new SliceImpl<>(userLikeDtos);

        Pageable pageable = PageRequest.of(0, 1);

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(recipeLikeRepository.userInfoRecipeLikes(member.getId(), null,pageable)).thenReturn(userDtoSlice);

        // 테스트 대상 메소드 호출
        UserInfoLikeResponse response = recipeLikeService.getUserLikesByPage(member.getId(),null,pageable);

        assertThat(response.getContent()).hasSize(2);
        assertThat(response.isNextPage()).isFalse();

        // recipeLikeRepository.userInfoRecipeLikes() 메소드가 한 번 호출되었는지 확인
        verify(recipeLikeRepository, times(1)).userInfoRecipeLikes(member.getId(), null,pageable);
    }
}