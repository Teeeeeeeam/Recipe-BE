package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.domain.BlackListRepository;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminsServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock RecipeRepository recipeRepository;
    @Mock PostRepository postRepository;
    @Mock BlackListRepository blackListRepository;
    @Mock NoticeRepository noticeRepository;
    @Mock RecipeBookmarkRepository recipeBookmarkRepository;
    @Mock JWTRefreshTokenRepository jwtRefreshTokenRepository;


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

        List<MemberDto> memberList = List.of( MemberDto.builder().username("회원1").email("email1").loginId(loginId).nickname("닉네임1").join_date(LocalDate.now()).build()
                , MemberDto.builder().username("회원2").email("email2").loginId("loginId2").nickname("닉네임2").join_date(LocalDate.now()).build());

        boolean hasNext = true;

        SliceImpl<MemberDto> memberSlice = new SliceImpl<>(memberList, pageRequest, hasNext);
        when(memberRepository.getMemberInfo(anyLong(), any(Pageable.class))).thenReturn(memberSlice);

        MemberInfoResponse memberInfoResponse = adminService.memberInfos(1l,pageRequest);

        assertThat(memberInfoResponse.getNextPage()).isTrue();
        assertThat(memberInfoResponse.getMemberInfos()).hasSize(2);
        assertThat(memberInfoResponse.getMemberInfos().get(0).getLoginId()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("관리자자가 사용자 데이터 삭제_처음삭제시")
    public void delete_user(){
        Long memberId= 1l;
        String email ="tes@eamil.com";
        boolean email_exist = true;

        Member member = Member.builder().id(memberId).email(email).nickName("닉네임").build();

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(blackListRepository.existsByEmail(eq(email))).thenReturn(email_exist);

        adminService.adminDeleteUser(memberId);

        verify(noticeRepository, times(1)).deleteByMember_Id(memberId);
        verify(recipeBookmarkRepository, times(1)).deleteByMember_Id(memberId);
        verify(jwtRefreshTokenRepository, times(1)).DeleteByMemberId(memberId);
        verify(memberRepository, times(1)).deleteById(memberId);

    }

    @Test
    @DisplayName("사용자 검색 무한 페이징")
    public void searchMembers_infinite_page(){
        Pageable pageRequest = PageRequest.of(0, 2);
        String loginId = "testId";

        List<MemberDto> memberList = List.of( MemberDto.builder().username("회원1").email("email1").loginId(loginId).nickname("닉네임1").join_date(LocalDate.now()).build()
                , MemberDto.builder().username("회원2").email("email2").loginId("loginId2").nickname("닉네임2").join_date(LocalDate.now()).build());

        boolean hasNext = true;

        SliceImpl<MemberDto> memberSlice = new SliceImpl<>(memberList, pageRequest, hasNext);
        when(memberRepository.searchMember(eq(loginId),eq("닉네임2"),isNull(),isNull(),isNull(),eq(pageRequest))).thenReturn(memberSlice);

        MemberInfoResponse memberInfoResponse = adminService.searchMember(loginId,"닉네임2",null,null,null,pageRequest);

        assertThat(memberInfoResponse.getNextPage()).isTrue();
        assertThat(memberInfoResponse.getMemberInfos()).hasSize(2);
        assertThat(memberInfoResponse.getMemberInfos().get(0).getLoginId()).isEqualTo(loginId);
    }
}