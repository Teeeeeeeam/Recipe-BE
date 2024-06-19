package com.team.RecipeRadar.domain.like_bookmark.application;


import com.team.RecipeRadar.domain.like_bookmark.application.like.PostLikeServiceImpl;
import com.team.RecipeRadar.domain.like_bookmark.dao.like.PostLikeRepository;
import com.team.RecipeRadar.domain.like_bookmark.domain.like.PostLike;
import com.team.RecipeRadar.domain.like_bookmark.dto.like.PostLikeRequest;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PostLikeServiceImplTest {


    @Mock PostLikeRepository postLikeRepository;
    @Mock NotificationService notificationService;
    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;

    @InjectMocks
    PostLikeServiceImpl postLikeService;;

    @Test
    @DisplayName("게시물 좋아요")
    void addLike_NewLikeAdded() {
        // Given
        Member member = Member.builder().id(1l).loginId("testId").nickName("테스트 닉네임").email("222").build();
        Post post = Post.builder()
                .id(2l)
                .postContent("컨텐트")
                .postTitle("타이틀")
                .postServing("ser")
                .postCookingTime("time")
                .postCookingLevel("level")
                .postLikeCount(0)
                .build();

        PostLikeRequest postLikeDto = PostLikeRequest.builder()
                .postId(2L)
                .build();

        when(memberRepository.findById(1l)).thenReturn(Optional.of(member));
        when(postRepository.findById(2l)).thenReturn(Optional.of(post));

        when(postLikeRepository.existsByMemberIdAndPostId(anyLong(), anyLong())).thenReturn(false);
        doNothing().when(notificationService).sendPostLikeNotification(any(),anyString());

        // When
        boolean result = postLikeService.addLike(postLikeDto,1l);

        // Then
        assertFalse(result); // 좋아요 추가되었으므로 false를 반환해야 함을 확인
        verify(postLikeRepository, times(1)).save(any(PostLike.class)); // save() 메서드가 한 번 호출되었는지 확인
        verify(postLikeRepository, never()).deleteByMemberIdAndPostId(anyLong(), anyLong()); // deleteByMember_IdAndPostId() 메서드가 호출되지 않았는지 확인
    }

    @Test
    @DisplayName("게시물의 좋아요존재시 좋아요 삭제")
    void addLike_ExistingLikeRemoved() {
        Long memberId = 1l;
        Long postId = 2l;
        Member member = Member.builder().id(memberId).nickName("닉네임").build();
        Post post = Post.builder()
                .id(postId)
                .postContent("컨텐트")
                .postTitle("타이틀")
                .postServing("ser")
                .member(member)
                .postCookingTime("time")
                .postCookingLevel("level")
                .postLikeCount(0)
                .build();

        PostLikeRequest postLikeDto = PostLikeRequest.builder()
                .postId(postId)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        when(postLikeRepository.existsByMemberIdAndPostId(eq(memberId), eq(postId))).thenReturn(true);
        doNothing().when(notificationService).deleteLikeNotification(anyLong(),anyLong(),anyLong());

        // When
        boolean result = postLikeService.addLike(postLikeDto,memberId);

        // Then
        assertTrue(result); // 좋아요가 이미 존재하므로 true를 반환해야 함을 확인
        verify(postLikeRepository, never()).save(any(PostLike.class)); // save() 메서드가 호출되지 않았는지 확인
        verify(postLikeRepository, times(1)).deleteByMemberIdAndPostId(anyLong(), anyLong()); // deleteByMember_IdAndPostId() 메서드가 한 번 호출되었는지 확인
    }

    @Test
    @DisplayName("좋아요 되어있는지 테스트")
    public void testCheckLike() {
        Long id = 1l;
        Long memberId = 2l;

        when(postLikeRepository.existsByMemberIdAndPostId(eq(memberId), anyLong())).thenReturn(true);

        assertTrue(postLikeService.checkLike(memberId, id));
    }

    @Test
    @DisplayName("좋아요 되어있는지 테스트")
    public void test_Check_UnLike() {
        Long id = 1l;

        when(postLikeRepository.existsByMemberIdAndPostId(anyLong(), anyLong())).thenReturn(false);

        assertFalse(postLikeService.checkLike(2l, id));
    }

}