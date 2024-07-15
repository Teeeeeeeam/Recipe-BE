package com.team.RecipeRadar.domain.bookmark.application;

import com.team.RecipeRadar.domain.bookmark.dto.response.UserInfoBookmarkResponse;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.bookmark.dao.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class RecipeBookmarkServiceImplTest {
    @Mock MemberRepository memberRepository;
    @Mock RecipeBookmarkRepository recipeBookmarkRepository;
    @Mock RecipeRepository recipeRepository;

    @InjectMocks RecipeBookmarkServiceImpl recipeBookmarkService;

    private Member member;
    private Recipe recipe;

    @BeforeEach
    void setUp(){
        member = Member.builder().id(1L).loginId("testId").username("testuserName").build();
        recipe = Recipe.builder().id(3L).title("title").cookingLevel("1").likeCount(1).build();
    }

    @Test
    @DisplayName("북마크 성공 테스트")
    void bookMark_Save_Success_Test(){
        when(memberRepository.findById(eq(1L))).thenReturn(Optional.of(member));
        when(recipeRepository.findById(eq(3L))).thenReturn(Optional.of(recipe));
        when(recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(member.getId(), recipe.getId())).thenReturn(false);

        Boolean result = recipeBookmarkService.saveBookmark(member.getId(), recipe.getId());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("이미 북마크한 레시피가 존재할 때 삭제 메서드가 실행되는지 테스트")
    void unBookMark_success_Test(){
        when(memberRepository.findById(eq(1L))).thenReturn(Optional.of(member));
        when(recipeRepository.findById(eq(3L))).thenReturn(Optional.of(recipe));
        when(recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(member.getId(), recipe.getId())).thenReturn(true);

        Boolean result = recipeBookmarkService.saveBookmark(member.getId(), recipe.getId());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("북마크 실패 예외 발생 테스트")
    void bookMark_Save_fail_Test(){
        when(memberRepository.findById(eq(1L))).thenThrow(new NoSuchElementException("사용자 및 레시피를 찾을 수 없습니다."));

        assertThatThrownBy(() -> recipeBookmarkService.saveBookmark(member.getId(), recipe.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("사용자 및 레시피를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("북마크를 했는지 확인하는 테스트")
    void bookmarkCheck(){
        when(recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(eq(2L), eq(3L))).thenReturn(true);

        Boolean checkBookmark = recipeBookmarkService.checkBookmark(2L, 3L);
        assertThat(checkBookmark).isTrue();
    }

    @Test
    @DisplayName("사용자가 즐겨찾기한 레시피 제목 조회 테스트")
    void userInfoBookmark(){
        Long memberId = 1L;

        Pageable pageRequest = PageRequest.of(0, 10);

        List<RecipeDto> list = List.of(
                RecipeDto.builder().id(1L).title("레시피1").build(),
                RecipeDto.builder().id(2L).title("레시피2").build(),
                RecipeDto.builder().id(3L).title("레시피3").build()
        );
        boolean hasNext = false;

        SliceImpl<RecipeDto> recipeDtoSlice = new SliceImpl<>(list, pageRequest, hasNext);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(recipeBookmarkRepository.userInfoBookmarks(eq(memberId), isNull(), eq(pageRequest))).thenReturn(recipeDtoSlice);

        UserInfoBookmarkResponse userInfoBookmarkResponse = recipeBookmarkService.userInfoBookmark(memberId, null, pageRequest);

        assertThat(userInfoBookmarkResponse.getBookmarkList()).hasSize(3);
        assertThat(userInfoBookmarkResponse.getNextPage()).isFalse();
    }
}