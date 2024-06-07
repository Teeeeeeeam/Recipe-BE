package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dao.RecipeLikeRepository;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.like.dto.*;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    /**
     * 레시피의 좋아요하는 로직 한번의 로직으로 좋아요 , 좋아요 해제를 할수있다.
     * @param recipeLikeDto 사용자 id, 레시피 id
     * @return 좋아요시 -> false , 좋아요 해제시 ->true
     */
    @Override
    public Boolean addLike(RecipeLikeDto recipeLikeDto) {
        Long memberId = recipeLikeDto.getMemberId();
        Long recipeId = recipeLikeDto.getRecipeId();
        boolean exists = recipeLikeRepository.existsByMemberIdAndRecipeId(memberId, recipeId);

        if (!exists){       //좋아요가 존재하지 않는다면
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("회원 " + memberId + "를 찾을수 없습니다."));
            Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new NoSuchElementException("레시피 " + recipeId + "를 찾을수 없습니다."));
            recipe.setLikeCount(recipe.getLikeCount()+1);       // 레시피 필드의 좋아요 수를 +1

            RecipeLike recipeLike = RecipeLike.builder()
                    .member(member)
                    .recipe(recipe).build();

            recipeRepository.save(recipe);
            recipeLikeRepository.save(recipeLike);

            return false;
        }else{      //좋아요가 존재한다면
            Recipe recipe = recipeRepository.findById(recipeId).get();
            recipe.setLikeCount(recipe.getLikeCount()-1);       // 레시피 필드의 좋아요 수를 -1
            recipeRepository.save(recipe);

            recipeLikeRepository.deleteByMemberIdAndRecipeId(memberId,recipeId);
            return true;
        }
    }

    /**
     * 해당 사용자가 해당 레시피의 좋아요를 했는지 확인하는 로직
     * @param jwtToken jwt토큰
     * @param Id   레시피 id
     * @return 좋아요가되어있다면 true. 되어있지 않다면 false
     */
    @Override
    public Boolean checkLike(String jwtToken, Long Id) {
        String loginId = jwtProvider.validateAccessToken(jwtToken);     //jwt 토큰 검증
        Member byLoginId = memberRepository.findByLoginId(loginId);
        Boolean aBoolean = recipeLikeRepository.existsByMemberIdAndRecipeId(byLoginId.getId(),Id );

        if (aBoolean){
            return true;
        }else
            return false;
    }

    /**
     * 좋아요한 레시피의 정보를 페이지의 정보를 Response로 반환하며, 해당 사용자만이 조회가능
     * @param authenticationName 시큐리티 홀더에 저장된 로그인한 사용자 이름
     * @param loginId 로그인 아이디
     * @param pageable 페이지 정보
     * @return 사용자의 좋아요 정보를 포함하는 UserInfoLikeResponse 객체
     */
    @Override
    public UserInfoLikeResponse getUserLikesByPage(String authenticationName, String loginId,Long recipeLike_lastId, Pageable pageable) {

        Member member = memberRepository.findByLoginId(loginId);

        if (member==null){              // 회원 정보가 없을 경우 예외 처리
            throw new NoSuchElementException("해당 회원을 찾을수 없습니다.");
        }

        if (!member.getUsername().equals(authenticationName)){   // 로그인 아이디와 JWT 토큰으로 인증된 아이디가 다를 경우 접근 권한 예외 처리
            throw new BadRequestException("접근할 수 없는 사용자입니다.");
        }

        Slice<UserLikeDto> userLikeDtos = recipeLikeRepository.userInfoRecipeLikes(member.getId(),recipeLike_lastId,pageable);

        boolean hasNext = userLikeDtos.hasNext();

        UserInfoLikeResponse build = UserInfoLikeResponse.builder()   // UserInfoLikeResponse 객체를 생성하여 데이터를 담아 반환
                .nextPage(hasNext)
                .content(userLikeDtos.getContent())
                .build();
        return build;
    }
}
