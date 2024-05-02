package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock PostRepository postRepository;
    @Mock MemberRepository memberRepository;

    @InjectMocks PostServiceImpl postService;

    @Test
    @DisplayName("사용자 페이지-작성한 게시글 조회 성공시 테스트")
    void userPostPage_success(){
        Long memberId = 1l;
        String auName="username";
        String loginId = "loginId";

        Member member = Member.builder().id(memberId).username("username").loginId(loginId).build();
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        Pageable pageable = PageRequest.of(0, 2);

        List<UserInfoPostRequest> requests = new ArrayList<>();
        requests.add(new UserInfoPostRequest(3l,"타이틀1"));
        requests.add(new UserInfoPostRequest(4l,"타이틀2"));

        SliceImpl<UserInfoPostRequest> userInfoPostRequests = new SliceImpl<>(requests, pageable, false);

        when(postRepository.userInfoPost(memberId,pageable)).thenReturn(userInfoPostRequests);

        UserInfoPostResponse userInfoPostResponse = postService.userPostPage(auName, loginId, pageable);
        assertThat(userInfoPostResponse.isNextPage()).isFalse();
        assertThat(userInfoPostResponse.getContent()).isNotEmpty();
        assertThat(userInfoPostResponse.getContent().size()).isEqualTo(2);
        assertThat(userInfoPostResponse.getContent().get(0).getPostTitle()).isEqualTo("타이틀1");
    }

    @Test
    @DisplayName("사용자 페이지-작성한 게시글 조회 실패시 테스트")
    void userPostPage_fail(){
        Long memberId = 1l;
        String auName="failUsername";
        String loginId = "loginId";

        Member member = Member.builder().id(memberId).username("username").loginId(loginId).build();
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        Pageable pageable = PageRequest.of(0, 2);

        assertThatThrownBy(() -> postService.userPostPage(auName, loginId, pageable)).isInstanceOf(AccessDeniedException.class);
    }

}