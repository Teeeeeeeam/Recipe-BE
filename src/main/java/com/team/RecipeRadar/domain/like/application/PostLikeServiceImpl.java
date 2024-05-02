package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dao.PostLikeRepository;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.dto.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.like.dto.PostLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

@Transactional
@RequiredArgsConstructor
@Qualifier("PostLikeServiceImpl")
@Service
@Slf4j
public class PostLikeServiceImpl<T extends PostLikeDto,U> implements LikeService<T> {

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
        }else
            return false;
    }

    /**
     * 커스텀한 response로 변환해서 전달
     * 
     * @param authenticationName 시큐리티 홀더에 저장된 로그인한 사용자 이름
     * @param loginId  조회할 회원의 ID
     * @param pageable 페이징 정보
     * @return 페이지별로 조회된 회원의 좋아요 정보를 포함하는 UserInfoLikeResponse 객체 반환
     */
    public UserInfoLikeResponse getUserLikesByPage(String authenticationName, String loginId, Pageable pageable) {


        Member member = memberRepository.findByLoginId(loginId);
        if (member==null){
            throw new NoSuchElementException("해당 회원을 찾을수 없습니다.");
        }
        if (!member.getUsername().equals(authenticationName)){
            throw new BadRequestException("접근할 수 없는 사용자입니다.");
        }

        Slice<UserLikeDto> userDtoSlice = postLikeRepository.userInfoLikes(member.getId(), pageable);

        boolean hasNext = userDtoSlice.hasNext();

        UserInfoLikeResponse likeResponse = UserInfoLikeResponse.builder()
                .content(userDtoSlice.getContent())
                .nextPage(hasNext)
                .build();
        return likeResponse;
    }


}
