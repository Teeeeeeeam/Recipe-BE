package com.team.RecipeRadar.domain.like.dao;

import com.team.RecipeRadar.domain.like.dao.RecipeLikeRepository;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
class RecipeLikeRepositoryTest {

    @Autowired
    RecipeLikeRepository recipeLikeRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired RecipeRepository recipeRepository;

    @Test
    @DisplayName("필드값이 존재하는지 테스트")
    @Rollback(value = false)
    void save(){
        Member member = Member.builder().loginId("loginId").build();
        Member member1 = Member.builder().loginId("loginId1").build();
        Recipe recipe = Recipe.builder().recipeContent("content").recipeTitle("title").cookingStep("cookingStep").recipeLevel("recipeLevel").recipeServing("recipeServing")
                .cookingTime("cookingTime").ingredientsAmount("ingredientsAmount").build();

        Member saveMember = memberRepository.save(member);
        Member saveMember1 = memberRepository.save(member1);

        Recipe saveRecipe = recipeRepository.save(recipe);

        RecipeLike build = RecipeLike.builder().member(saveMember).recipe(saveRecipe).build();
        RecipeLike build1 = RecipeLike.builder().member(saveMember1).recipe(saveRecipe).build();

        RecipeLike save = recipeLikeRepository.save(build);
        RecipeLike save1 = recipeLikeRepository.save(build1);

        assertThat(save).isNotNull();
        assertThat(save1).isNotNull();

        assertThat(save.getMember().getId()).isEqualTo(saveMember.getId());
        assertThat(save1.getMember().getId()).isEqualTo(saveMember1.getId());

        boolean exists = recipeLikeRepository.existsByMemberIdAndRecipeId(saveMember.getId(), saveRecipe.getId());
        assertThat(exists).isTrue();

        boolean existsFalse = recipeLikeRepository.existsByMemberIdAndRecipeId(saveMember.getId(), 5l);
        assertThat(existsFalse).isFalse();

    }
    
    @Test
    @DisplayName("삭제 테스트")
    @Rollback(value = false)
    void delete(){
        Member member = Member.builder().loginId("loginId").build();
        Member member1 = Member.builder().loginId("loginId1").build();
        Recipe recipe = Recipe.builder().recipeContent("content").recipeTitle("title").cookingStep("cookingStep").recipeLevel("recipeLevel").recipeServing("recipeServing")
                .cookingTime("cookingTime").ingredientsAmount("ingredientsAmount").build();

        Member saveMember = memberRepository.save(member);
        Member saveMember1 = memberRepository.save(member1);

        Recipe saveRecipe = recipeRepository.save(recipe);

        RecipeLike build = RecipeLike.builder().member(saveMember).recipe(saveRecipe).build();
        RecipeLike build1 = RecipeLike.builder().member(saveMember1).recipe(saveRecipe).build();

        RecipeLike save = recipeLikeRepository.save(build);
        RecipeLike save1 = recipeLikeRepository.save(build1);


        recipeLikeRepository.deleteByMemberIdAndRecipeId(saveMember.getId(),saveRecipe.getId());

        Optional<RecipeLike> byId = recipeLikeRepository.findById(save.getId());
        assertThat(byId).isEmpty();

        Optional<RecipeLike> byId1 = recipeLikeRepository.findById(save1.getId());
        assertThat(byId1).isNotEmpty();


    }

}