package com.team.RecipeRadar.domain.like.application;

import com.team.RecipeRadar.domain.like.dao.like.PostLikeRepository;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.dto.like.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.dto.like.UserLikeDto;
import com.team.RecipeRadar.domain.like.dto.like.PostLikeRequest;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

import static com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType.*;

@Transactional
@RequiredArgsConstructor
@Qualifier("PostLikeServiceImpl")
@Service
@Slf4j
public class PostLikeServiceImpl<T extends PostLikeRequest,U> implements LikeService<T> {

    private final PostLikeRepository postLikeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final JwtProvider jwtProvider;
    private final NotificationService notificationService;

    /**
     * 좋아요 api 호출시 해당 게시글의 좋아요가 있으면 DB 에서 삭제하고 좋아요가 되어있지않다면 DB에 추가하는 식으로 구현
     * @param postLikeRequest
     * @return
     */
    @Override
    public Boolean addLike(PostLikeRequest postLikeRequest,Long memberId) {

        Boolean alreadyLiked  = postLikeRepository.existsByMemberIdAndPostId(memberId, postLikeRequest.getPostId());    // 해당 테이블의 있는지검사
        Post post = postRepository.findById(postLikeRequest.getPostId()).orElseThrow(() -> new NoSuchDataException(NO_SUCH_POST));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NO_SUCH_MEMBER));

        if (alreadyLiked) {
            removeLike(post, member);
        } else {
            addLike(post, member);
        }

        return alreadyLiked;
    }

    @Override
    public Boolean checkLike(Long memberId,Long postId) {
        Boolean alreadyLiked = postLikeRepository.existsByMemberIdAndPostId(memberId,postId);

        return alreadyLiked;
    }

    /**
     * 커스텀한 response로 변환해서 전달
     *
     * @param memberId 조회할 회원의 ID
     * @param pageable 페이징 정보
     * @return 페이지별로 조회된 회원의 좋아요 정보를 포함하는 UserInfoLikeResponse 객체 반환
     */
    public UserInfoLikeResponse getUserLikesByPage(Long memberId,Long postLike_lastId, Pageable pageable) {


        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NO_SUCH_MEMBER));
        if (member==null){
            throw new NoSuchElementException("해당 회원을 찾을수 없습니다.");
        }

        Slice<UserLikeDto> userDtoSlice = postLikeRepository.userInfoLikes(member.getId(),postLike_lastId ,pageable);

        boolean hasNext = userDtoSlice.hasNext();

        UserInfoLikeResponse likeResponse = UserInfoLikeResponse.builder()
                .content(userDtoSlice.getContent())
                .nextPage(hasNext)
                .build();
        return likeResponse;
    }


    private void addLike(Post post, Member member) {
        post.setPostLikeCount(post.getPostLikeCount() + 1);
        postRepository.save(post);
        postLikeRepository.save(PostLike.createPostLIke(post, member));
        notificationService.sendPostLikeNotification(post, member.getNickName());
    }

    private void removeLike(Post post, Member member) {
        post.setPostLikeCount(post.getPostLikeCount() - 1);
        postRepository.save(post);
        notificationService.deleteLikeNotification(member.getId(), post.getMember().getId(), post.getId());
        postLikeRepository.deleteByMemberIdAndPostId(member.getId(), post.getId());
    }
}
