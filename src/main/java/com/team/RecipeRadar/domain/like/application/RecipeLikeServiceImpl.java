package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dao.RecipeLikeRepository;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.like.dto.RecipeLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional
@RequiredArgsConstructor
@Qualifier("RecipeLikeServiceImpl")
@Service
@Slf4j
public class RecipeLikeServiceImpl<T extends RecipeLikeDto> implements LikeService<T> {

    private final MemberRepository memberRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;
    private final JwtProvider jwtProvider;

    @Override
    public Boolean addLike(RecipeLikeDto recipeLikeDto) {
        Long memberId = recipeLikeDto.getMemberId();
        Long recipeId = recipeLikeDto.getRecipeId();
        boolean exists = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipeId);

        if (!exists){
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("회원 " + memberId + "를 찾을수 없습니다."));
            Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new NoSuchElementException("레시피 " + recipeId + "를 찾을수 없습니다."));
            recipe.setLikeCount(recipe.getLikeCount()+1);

            RecipeLike recipeLike = RecipeLike.builder()
                    .member(member)
                    .recipe(recipe).build();

            recipeRepository.save(recipe);
            recipeLikeRepository.save(recipeLike);

            return false;
        }else{
            Recipe recipe = recipeRepository.findById(recipeId).get();
            recipe.setLikeCount(recipe.getLikeCount()-1);
            recipeRepository.save(recipe);

            recipeLikeRepository.deleteByMemberIdAndRecipeId(memberId,recipeId);
            return true;
        }
    }

    @Override
    public Boolean checkLike(String jwtToken, Long postId) {
        String loginId = jwtProvider.validateAccessToken(jwtToken);
        log.info("로그인아이디={}",loginId);
        Member byLoginId = memberRepository.findByLoginId(loginId);
        log.info("멤바={}",byLoginId);
        Boolean aBoolean = recipeLikeRepository.existsByMemberIdAndRecipeId(byLoginId.getId(),postId );
        log.info("aaasdad={}",aBoolean);
        if (aBoolean){
            return true;
        }else
            return false;
    }
}
