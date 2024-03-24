package com.team.RecipeRadar.domain.like.postLike.application;

import com.team.RecipeRadar.domain.like.ex.LikeException;
import com.team.RecipeRadar.domain.like.postLike.dao.PostLikeRepository;
import com.team.RecipeRadar.domain.like.postLike.domain.PostLike;
import com.team.RecipeRadar.domain.like.postLike.dto.PostLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class PostLikeServiceImpl implements PostLikeService{

    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final JwtProvider jwtProvider;

    /**
     * 좋아요 api 호출시 해당 게시글의 좋아요가 있으면 DB 에서 삭제하고 좋아요가 되어있지않다면 DB에 추가하는 식으로 구현
     * @param postLikeDto
     * @return
     */
    @Override
    public Boolean addLike(PostLikeDto postLikeDto) {

        Boolean aBoolean = postLikeRepository.existsByMemberIdAndPostId(postLikeDto.getMemberId(), postLikeDto.getPostId());    // 해당 테이블의 있는지검사

        if (!aBoolean) {
            Member member = memberRepository.findById(postLikeDto.getMemberId()).orElseThrow(() -> new NoSuchElementException("회원을 찾을 수가 없습니다."));
            Post post = postRepository.findById(postLikeDto.getPostId()).orElseThrow(() -> new NoSuchElementException("게시물을 찾을 수없습니다."));
            post.setPostLikeCount(post.getPostLikeCount()+1);
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .member(member)
                    .build();

            postRepository.save(post);
            postLikeRepository.save(postLike);
            log.info("댓글 등록");
            return false;
        }else{
            Post post = postRepository.findById(postLikeDto.getPostId()).get();
            post.setPostLikeCount(post.getPostLikeCount()-1);
            postRepository.save(post);

            postLikeRepository.deleteByMemberIdAndPostId(postLikeDto.getMemberId(),postLikeDto.getPostId());
            log.info("댓글 삭제");
            return true;
        }
    }

    @Override
    public Boolean checkLike(String jwtToken, Long postId) {

        String loginId = jwtProvider.validateAccessToken(jwtToken);
        Member byLoginId = memberRepository.findByLoginId(loginId);
        Boolean aBoolean = postLikeRepository.existsByMemberIdAndPostId(byLoginId.getId(),postId );
        if (aBoolean){
            return true;
        }
        return false;
    }

}
