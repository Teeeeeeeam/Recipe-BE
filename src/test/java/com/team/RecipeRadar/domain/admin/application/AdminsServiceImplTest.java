package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminsServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock RecipeRepository recipeRepository;
    @Mock PostRepository postRepository;

    @InjectMocks AdminsServiceImpl adminService;

    @Test
    @DisplayName("전체 회원수 조회")
    void count_members(){
        long count =10;

        when(memberRepository.countAllBy()).thenReturn(count);

        long l = adminService.searchAllMembers();
        assertThat(l).isEqualTo(count);
    }

    @Test
    @DisplayName("전체 요리글수 조회")
    void count_Recipes(){
        long count =1123123123;

        when(recipeRepository.countAllBy()).thenReturn(count);

        long l = adminService.searchAllRecipes();
        assertThat(l).isEqualTo(count);
    }

    @Test
    @DisplayName("전체 게시글 조회")
    void count_posts(){
        long count =550;

        when(postRepository.countAllBy()).thenReturn(count);

        long l = adminService.searchAllPosts();
        assertThat(l).isEqualTo(count);
    }

    @Test
    @DisplayName("전체 사용자 무한 페이징")
    public void infinite_page(){
        Pageable pageRequest = PageRequest.of(0, 2);
        String loginId = "testId";

        List<Member> memberList = List.of( Member.builder().username("회원1").email("email1").loginId(loginId).nickName("닉네임1").join_date(LocalDate.now()).build()
                , Member.builder().username("회원2").email("email2").loginId("loginId2").nickName("닉네임2").join_date(LocalDate.now()).build());

        boolean hasNext = true;

        SliceImpl<Member> memberSlice = new SliceImpl<>(memberList, pageRequest, hasNext);
        when(memberRepository.getMemberInfo(pageRequest)).thenReturn(memberSlice);

        MemberInfoResponse memberInfoResponse = adminService.memberInfos(pageRequest);

        assertThat(memberInfoResponse.getNextPage()).isTrue();
        assertThat(memberInfoResponse.getMemberInfos()).hasSize(2);
        assertThat(memberInfoResponse.getMemberInfos().get(0).getLoginId()).isEqualTo(loginId);
    }
}