package com.team.RecipeRadar.domain.like.dao;

import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
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
        Recipe recipe = Recipe.builder().id(3l).content("content").likeCount(0).build();


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
        Recipe recipe = Recipe.builder().id(3l).content("content").likeCount(0).build();


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

    @Test
    @DisplayName("사용자페이지의 레시피 좋아요 정보를 무한 스크롤 조회")
    void testUserInfoLikesPaging() {
        Member member = memberRepository.save(Member.builder().loginId("testId").build());
        Recipe recipe = recipeRepository.save(Recipe.builder().content("테스트 레시피1").title("타이틀1").postNumber("넘버").cookingTime("쿠킹 시간").likeCount(1).build());
        Recipe recipe1 = recipeRepository.save(Recipe.builder().content("테스트 레시피2").title("타이틀2").postNumber("넘버").cookingTime("쿠킹 시간").likeCount(1).build());

        recipeLikeRepository.save(RecipeLike.builder().member(member).recipe(recipe).build());
        recipeLikeRepository.save(RecipeLike.builder().member(member).recipe(recipe1).build());

        int pageSize = 1; // 페이지당 크기
        int pageNumber = 0; // 페이지 번호

        // 사용자의 좋아요 정보를 첫 번째 페이지로 조회
        Slice<UserLikeDto> result = recipeLikeRepository.userInfoRecipeLikes(member.getId(), PageRequest.of(pageNumber, pageSize));
        List<UserLikeDto> content = result.getContent();
        log.info("res={}",result.hasNext());
        log.info("res={}",result.getContent().stream().toList());

        assertThat(content).hasSize(1); // 페이지 크기와 일치하는지 확인
        assertThat(result.hasNext()).isTrue(); // 다음 페이지가 있는지 확인

        // 다음 페이지로 넘어가기
        pageNumber++;
        result = recipeLikeRepository.userInfoRecipeLikes(member.getId(), PageRequest.of(pageNumber, pageSize));
        content = result.getContent();

        assertThat(content).hasSize(1); // 페이지 크기와 일치하는지 확인
        assertThat(result.hasNext()).isFalse(); // 다음 페이지가 없는지 확인
    }
}