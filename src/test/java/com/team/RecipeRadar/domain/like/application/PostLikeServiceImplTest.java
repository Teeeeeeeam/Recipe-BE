package com.team.RecipeRadar.domain.like.application;


import com.team.RecipeRadar.domain.like.dao.like.PostLikeRepository;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.dto.request.PostLikeRequest;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceImplTest {


    @Mock PostLikeRepository postLikeRepository;
    @Mock NotificationService notificationService;
    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;

    @InjectMocks
    PostLikeServiceImpl postLikeService;;


    private Member member;
    private List<Post> posts;
    @BeforeEach
    void setUp(){
        member = Member.builder().id(1l).loginId("testId").nickName("테스트 닉네임").build();
        posts = List.of(
                Post.builder().id(1l).postTitle("첫번째 게시글").postLikeCount(10).member(member).build(),
                Post.builder().id(2l).postTitle("두번째 게시글").postLikeCount(24).member(member).build());
    }
    @Test
    @DisplayName("게시물 좋아요")
    void addLike_NewLikeAdded() {
        PostLikeRequest postLikeDto = PostLikeRequest.builder()
                .postId(2L)
                .build();

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(posts.get(0)));

        when(postLikeRepository.existsByMemberIdAndPostId(anyLong(), anyLong())).thenReturn(false);
        doNothing().when(notificationService).sendPostLikeNotification(any(),anyString());

        boolean result = postLikeService.addLike(postLikeDto,member.getId());

        assertThat(result).isFalse();
        verify(postLikeRepository, times(1)).save(any(PostLike.class)); // save() 메서드가 한 번 호출되었는지 확인
        verify(postLikeRepository, never()).deleteByMemberIdAndPostId(anyLong(), anyLong()); // deleteByMember_IdAndPostId() 메서드가 호출되지 않았는지 확인
    }

    @Test
    @DisplayName("게시물의 좋아요존재시 좋아요 삭제")
    void addLike_ExistingLikeRemoved() {
        PostLikeRequest postLikeDto = PostLikeRequest.builder()
                .postId(posts.get(0).getId())
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(posts.get(0)));
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        when(postLikeRepository.existsByMemberIdAndPostId(anyLong(), anyLong())).thenReturn(true);
        doNothing().when(notificationService).deleteLikeNotification(anyLong(),anyLong(),anyLong());

        boolean result = postLikeService.addLike(postLikeDto,member.getId());

        assertThat(result).isTrue();
        verify(postLikeRepository, never()).save(any(PostLike.class)); // save() 메서드가 호출되지 않았는지 확인
        verify(postLikeRepository, times(1)).deleteByMemberIdAndPostId(anyLong(), anyLong()); // deleteByMember_IdAndPostId() 메서드가 한 번 호출되었는지 확인
    }

    @Test
    @DisplayName("좋아요 되어있는지 테스트")
    public void testCheckLike() {
        when(postLikeRepository.existsByMemberIdAndPostId(eq(member.getId()), anyLong())).thenReturn(true);

        assertThat(postLikeService.checkLike(member.getId(),posts.get(0).getId())).isTrue();
        assertThat(postLikeService.checkLike(2l,posts.get(0).getId())).isFalse();
    }

}