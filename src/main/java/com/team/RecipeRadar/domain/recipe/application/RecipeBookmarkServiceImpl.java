package com.team.RecipeRadar.domain.recipe.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.domain.RecipeBookmark;
import com.team.RecipeRadar.global.exception.ex.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_RECIPE));

        boolean alreadyBookmark = recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(member.getId(), recipe.getId());
        if (!alreadyBookmark){
            addBookmark(member, recipe);
        }else
            removeBookmark(member, recipe);

        return alreadyBookmark;
    }

    private void removeBookmark(Member member, Recipe recipe) {
        recipeBookmarkRepository.deleteByMember_IdAndRecipe_Id(member.getId(), recipe.getId());
    }

    private void addBookmark(Member member, Recipe recipe) {
        recipeBookmarkRepository.save(RecipeBookmark.toEntity(member, recipe));
    }

    @Override
    public Boolean checkBookmark(Long memberId, Long recipeId) {
        return recipeBookmarkRepository.existsByMember_IdAndRecipe_Id(memberId,recipeId);
    }


}
