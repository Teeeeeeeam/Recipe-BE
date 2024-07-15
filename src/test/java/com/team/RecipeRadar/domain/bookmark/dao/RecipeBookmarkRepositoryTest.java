package com.team.RecipeRadar.domain.bookmark.dao;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.bookmark.domain.RecipeBookmark;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class RecipeBookmarkRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired RecipeRepository recipeRepository;
    @Autowired RecipeBookmarkRepository recipeBookmarkRepository;

    private Member member;
    private Recipe recipe;
    private Member saveMember;
    private Recipe saveRecipe;

    @BeforeEach
    void setUp() {
        member = Member.builder().loginId("Test").username("username").build();
        recipe = Recipe.builder().likeCount(1).title("title").build();
        saveMember = memberRepository.save(member);
        saveRecipe = recipeRepository.save(recipe);
    }

    @Test
    @DisplayName("즐겨찾기에 사용자와 해당 레시피가 존재하는지 확인하는 테스트")
    void exist_memberANDRecipe() {
        RecipeBookmark recipeBookmark = RecipeBookmark.toEntity(saveMember, saveRecipe);
        recipeBookmarkRepository.save(recipeBookmark);

        boolean test = recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(saveMember.getId(), saveRecipe.getId());

        assertThat(test).isTrue();
    }

    @Test
    @DisplayName("즐겨찾기에 사용자와 해당 레시피가 존재하지 않을때")
    void exist_memberANDRecipe_fails() {
        RecipeBookmark recipeBookmark = RecipeBookmark.toEntity(saveMember, saveRecipe);
        recipeBookmarkRepository.save(recipeBookmark);

        boolean test = recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(saveMember.getId(), 2L); // 존재하지 않는 레시피 ID 사용

        assertThat(test).isFalse();
    }

    @Test
    @DisplayName("즐겨찾기에서 즐겨찾기 삭제(해제)")
    void unBookMark_test() {
        RecipeBookmark recipeBookmark = RecipeBookmark.toEntity(saveMember, saveRecipe);
        recipeBookmarkRepository.save(recipeBookmark);

        recipeBookmarkRepository.deleteByMember_IdAndRecipe_Id(saveMember.getId(), saveRecipe.getId());

        assertThat(recipeBookmarkRepository.findAll()).isEmpty();
    }
}