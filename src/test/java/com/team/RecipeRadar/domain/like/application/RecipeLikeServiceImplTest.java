package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dao.RecipeLikeRepository;
import com.team.RecipeRadar.domain.like.dto.RecipeLikeRequest;
import com.team.RecipeRadar.domain.like.dto.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class RecipeLikeServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock RecipeLikeRepository recipeLikeRepository;
    @Mock RecipeRepository recipeRepository;

    @InjectMocks
    RecipeLikeServiceImpl recipeLikeService;

    @Test
    @DisplayName("레시피 좋아요")
    void add_recipe_like(){
        Long memberId = 1l;
        Long recipe_id= 3l;
        Member member = Member.builder().id(memberId).loginId("testId").username("testuserName").build();
        Recipe recipe = Recipe.builder().id(recipe_id).title("title").cookingLevel("1").likeCount(0).build();

        RecipeLikeRequest build = RecipeLikeRequest.builder().recipeId(recipe_id).build();


        when(recipeLikeRepository.existsByMemberIdAndRecipeId(memberId,build.getRecipeId())).thenReturn(false);      // 좋아요가 되어있지않음
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));


        Boolean aBoolean = recipeLikeService.addLike(build,memberId);

        assertThat(aBoolean).isFalse();
        assertThat(recipe.getLikeCount()).isEqualTo(1);         //결국은 0 에서 하나증간된 1로 변경

        verify(recipeLikeRepository, times(0)).deleteByMemberIdAndRecipeId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("레시피 좋아요되어있을때")
    void add_recipe_like_delete(){
        Long memberId = 1l;
        Long recipe_id= 3l;
        Member member = Member.builder().id(memberId).loginId("testId").build();
        Recipe recipe = Recipe.builder().id(recipe_id).likeCount(1).build();

        RecipeLikeRequest build = RecipeLikeRequest.builder().recipeId(recipe_id).build();

        when(recipeLikeRepository.existsByMemberIdAndRecipeId(memberId,build.getRecipeId())).thenReturn(true);
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.of(recipe));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        Boolean aBoolean = recipeLikeService.addLike(build,memberId);

        assertThat(aBoolean).isTrue();
        assertThat(recipe.getLikeCount()).isEqualTo(0);
        verify(recipeLikeRepository, times(1)).deleteByMemberIdAndRecipeId(anyLong(), anyLong());
    }


    @Test
    @DisplayName("좋아요 되어있는지 테스트")
    public void testCheckLike() {
        Long id = 1l;
        Long memberId = 2l;

        when(recipeLikeRepository.existsByMemberIdAndRecipeId(anyLong(), anyLong())).thenReturn(true);

        assertTrue(recipeLikeService.checkLike(memberId, id));
    }

    @Test
    @DisplayName("정상적으로 회원의 좋아요 정보를 페이지별로 가져오는지 확인")
    void Test_Get_User_LikesByPage() {
        // 테스트에 필요한 가짜 데이터 생성
        Long memberId = 1l;
        String memberName="testName";
        Member member = Member.builder().id(1l).loginId("test").username(memberName).build();

        List<UserLikeDto> userLikeDtos = new ArrayList<>();
        userLikeDtos.add(new UserLikeDto(1L, 1l,"Content 1", "Title 1"));
        userLikeDtos.add(new UserLikeDto(2L, 1l,"Content 2", "Title 2"));

        Slice<UserLikeDto> userDtoSlice = new SliceImpl<>(userLikeDtos);


        Pageable pageable = PageRequest.of(0, 1);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(recipeLikeRepository.userInfoRecipeLikes(member.getId(), null,pageable)).thenReturn(userDtoSlice);

        // 테스트 대상 메소드 호출
        UserInfoLikeResponse response = recipeLikeService.getUserLikesByPage(memberId,null,pageable);

        // 결과 검증
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.isNextPage()).isFalse();

        // recipeLikeRepository.userInfoRecipeLikes() 메소드가 한 번 호출되었는지 확인
        verify(recipeLikeRepository, times(1)).userInfoRecipeLikes(member.getId(), null,pageable);
    }
}