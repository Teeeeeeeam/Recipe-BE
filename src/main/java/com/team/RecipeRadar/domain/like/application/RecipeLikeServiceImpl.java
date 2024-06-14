package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dao.RecipeLikeRepository;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.like.dto.*;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.exception.ex.NoSuchDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.team.RecipeRadar.global.exception.ex.NoSuchErrorType.NO_SUCH_MEMBER;
import static com.team.RecipeRadar.global.exception.ex.NoSuchErrorType.NO_SUCH_RECIPE;

@Transactional
@RequiredArgsConstructor
@Qualifier("RecipeLikeServiceImpl")
@Service
@Slf4j
public class RecipeLikeServiceImpl<T extends RecipeLikeRequest> implements LikeService<T> {

    private final MemberRepository memberRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeRepository recipeRepository;

    /**
     * 레시피의 좋아요하는 로직 한번의 로직으로 좋아요 , 좋아요 해제를 할수있다.
     * @param recipeLikeRequest 레시피 id
     * @param memberId
     * @return 좋아요시 -> true , 좋아요 해제시 -> false
     */
    @Override
    public Boolean addLike(RecipeLikeRequest recipeLikeRequest, Long memberId) {
        boolean alreadyLiked = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipeLikeRequest.getRecipeId());

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NO_SUCH_MEMBER));
        Recipe recipe = recipeRepository.findById(recipeLikeRequest.getRecipeId()).orElseThrow(() -> new NoSuchDataException(NO_SUCH_RECIPE));

        if (!alreadyLiked){       //좋아요가 존재하지 않는다면
            addLike(recipe,member);
        }else{      //좋아요가 존재한다면
            removeLike(recipe,member);
        }
        return alreadyLiked;
    }

    /**
     * 해당 사용자가 해당 레시피의 좋아요를 했는지 확인하는 로직
     * @param memberId 로그인한 사용자 Id
     * @param recipeId  레시피 id
     * @return 좋아요가되어있다면 true. 되어있지 않다면 false
     */
    @Override
    public Boolean checkLike(Long memberId, Long recipeId) {
        return recipeLikeRepository.existsByMemberIdAndRecipeId(memberId,recipeId );
    }

    /**
     * 좋아요한 레시피의 정보를 페이지의 정보를 Response로 반환하며, 해당 사용자만이 조회가능
     * @param memberId 로그인 아이디
     * @param pageable 페이지 정보
     * @return 사용자의 좋아요 정보를 포함하는 UserInfoLikeResponse 객체
     */
    @Override
    public UserInfoLikeResponse getUserLikesByPage(Long memberId,Long recipeLike_lastId, Pageable pageable) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NO_SUCH_MEMBER));

        Slice<UserLikeDto> userLikeDtoSlice = recipeLikeRepository.userInfoRecipeLikes(member.getId(),recipeLike_lastId,pageable);

        boolean hasNext = userLikeDtoSlice.hasNext();

        return UserInfoLikeResponse.builder()   // UserInfoLikeResponse 객체를 생성하여 데이터를 담아 반환
                .nextPage(hasNext)
                .content(userLikeDtoSlice.getContent())
                .build();
    }

    private void addLike(Recipe recipe, Member member) {
        recipe.setLikeCount(recipe.getLikeCount()+1);       // 레시피 필드의 좋아요 수를 +1
        recipeRepository.save(recipe);
        recipeLikeRepository.save(RecipeLike.createRecipeLike(member,recipe));
    }

    private void removeLike(Recipe recipe, Member member) {
        recipe.setLikeCount(recipe.getLikeCount()-1);       // 레시피 필드의 좋아요 수를 -1
        recipeRepository.save(recipe);
        recipeLikeRepository.deleteByMemberIdAndRecipeId(member.getId(),recipe.getId());
    }
}
