package com.team.RecipeRadar.domain.recipe.dao.bookmark;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.domain.RecipeBookmark;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
@Import(QueryDslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
@Slf4j
class RecipeBookmarkRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    RecipeBookmarkRepository recipeBookmarkRepository;

    @Test
    @DisplayName("즐겨찾기에 사용자와 해당 레시피가 존재하는지 확인하는 테스트")
    void exist_memberANDRecipe(){
        Member member = Member.builder().loginId("Test").username("loginId").username("username").build();
        Recipe recipe = Recipe.builder().likeCount(1).content("content").title("title").build();

        Member saveMember = memberRepository.save(member);
        Recipe saveRecipe = recipeRepository.save(recipe);

        RecipeBookmark recipeBookmark = RecipeBookmark.toEntity(saveMember, saveRecipe);
        recipeBookmarkRepository.save(recipeBookmark);

        boolean test = recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(saveMember.getId(), saveRecipe.getId());

        assertThat(test).isTrue();
    }

    @Test
    @DisplayName("즐겨찾기에 사용자와 해당 레시피가 존재하지 않을때")
    void exist_memberANDRecipe_fails(){
        Member member = Member.builder().loginId("Test").username("loginId").username("username").build();
        Recipe recipe = Recipe.builder().likeCount(1).content("content").title("title").build();
        Member saveMember = memberRepository.save(member);
        Recipe saveRecipe = recipeRepository.save(recipe);

        RecipeBookmark recipeBookmark = RecipeBookmark.toEntity(saveMember, saveRecipe);
        recipeBookmarkRepository.save(recipeBookmark);
        boolean test = recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(member.getId(), 2l); // 존재하지 않는 레시피 ID 사용

        assertThat(test).isFalse();
    }

    @Test
    @DisplayName("즐겨찾기에서 즐겨찾기 삭제(해제)")
    void unBookMark_test(){
        Member member = Member.builder().loginId("Test").username("loginId").username("username").build();
        Recipe recipe = Recipe.builder().likeCount(1).content("content").title("title").build();
        Member saveMember = memberRepository.save(member);
        Recipe saveRecipe = recipeRepository.save(recipe);


        RecipeBookmark recipeBookmark = RecipeBookmark.toEntity(saveMember, saveRecipe);
        recipeBookmarkRepository.save(recipeBookmark);

        recipeBookmarkRepository.deleteByMember_IdAndRecipe_Id(member.getId(), recipe.getId());

        assertThat(recipeBookmarkRepository.findAll()).isEmpty();
    }
}