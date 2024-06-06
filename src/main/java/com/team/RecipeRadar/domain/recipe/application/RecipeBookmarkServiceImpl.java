package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.domain.RecipeBookmark;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeBookmarkServiceImpl implements RecipeBookmarkService{

    private final RecipeBookmarkRepository recipeBookmarkRepository;
    private final MemberRepository memberRepository;
    private final RecipeRepository recipeRepository;

    /**
     * 즐겨찾기를 동적으로 실행하는 로직 false일떄는 즐겨찾기 성공, true일때는 즐겨찾기 해제
     * @param memberId  로그인한 사용자 id
     * @param recipeId  현재 즐겨 찾기를할 레시피
     * @return
     */
    @Override
    public Boolean saveBookmark(Long memberId, Long recipeId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("사용자 및 레시피를 찾을수 없습니다."));
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new NoSuchElementException("사용자 및 레시피를 찾을수 없습니다."));

        boolean exists = recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(member.getId(), recipe.getId());
        if (!exists){
            recipeBookmarkRepository.save(RecipeBookmark.toEntity(member,recipe));
            return true;
        }else {
            recipeBookmarkRepository.deleteByMember_IdAndRecipe_Id(member.getId(), recipe.getId());
            return false;
        }
    }
    @Override
    public Boolean checkBookmark(Long memberId, Long recipeId) {
        return recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(memberId,recipeId);
    }


}
