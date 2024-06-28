package com.team.RecipeRadar.domain.like.dao;

import com.team.RecipeRadar.domain.like.dao.like.RecipeLikeRepository;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(QueryDslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class RecipeLikeRepositoryTest {

    @Autowired RecipeLikeRepository recipeLikeRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired RecipeRepository recipeRepository;
    @Autowired EntityManager em;

    private List<Member> members;
    private List<Recipe> recipes;
    private List<RecipeLike> recipeLikes;

    @BeforeEach
    void setUp(){
        members = List.of(
                Member.builder().loginId("testId1").username("testname1").build(),
                Member.builder().loginId("testId2").username("testname2").build()
        );
        memberRepository.saveAll(members);

        recipes =List.of(
                Recipe.builder().title("타이틀1").cookingTime("쿠킹 시간").likeCount(1).build(),
                Recipe.builder().title("타이틀2").cookingTime("쿠킹 시간").likeCount(2).build(),
                Recipe.builder().title("타이틀3").cookingTime("쿠킹 시간").likeCount(5).build());
        recipeRepository.saveAll(recipes);

        recipeLikes =List.of(
                RecipeLike.builder().recipe(recipes.get(0)).member(members.get(0)).build(),
                RecipeLike.builder().recipe(recipes.get(1)).member(members.get(0)).build(),
                RecipeLike.builder().recipe(recipes.get(2)).member(members.get(0)).build()
        );
        recipeLikeRepository.saveAll(recipeLikes);
    }

    @Test
    @DisplayName("해당 레시피의 좋아요가 되어있는지 체크")
    void save(){
        boolean exists = recipeLikeRepository.existsByMemberIdAndRecipeId(members.get(0).getId(), recipes.get(0).getId());
        assertThat(exists).isTrue();

        boolean existsFalse = recipeLikeRepository.existsByMemberIdAndRecipeId(members.get(0).getId(),5l);
        assertThat(existsFalse).isFalse();
    }
    
    @Test
    @DisplayName("사용자, 레시피Id 일치 시 삭제 테스트")
    void delete(){
        recipeLikeRepository.deleteByMemberIdAndRecipeId(members.get(0).getId(),recipes.get(0).getId());
        em.flush();

        List<RecipeLike> all = recipeLikeRepository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("사용자페이지의 레시피 좋아요 정보를 무한 스크롤 조회")
    void testUserInfoLikesPaging() {
        Slice<UserLikeDto> result = recipeLikeRepository.userInfoRecipeLikes(members.get(0).getId(), null,Pageable.ofSize(2));
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
    }
    @Test
    @DisplayName("레시피 아이디가 일치하하는 엔티티 삭제")
    void onlyRecipeId(){
        recipeLikeRepository.deleteRecipeId(recipes.get(0).getId());
        em.flush();

        List<RecipeLike> all = recipeLikeRepository.findAll();
        assertThat(all).hasSize(2);
    }
}