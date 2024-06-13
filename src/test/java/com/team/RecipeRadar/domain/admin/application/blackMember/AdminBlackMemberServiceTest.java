package com.team.RecipeRadar.domain.admin.application.blackMember;

import com.team.RecipeRadar.domain.admin.dao.BlackList;
import com.team.RecipeRadar.domain.admin.domain.BlackListRepository;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminBlackMemberServiceTest {

    @Mock MemberRepository memberRepository;
    @Mock BlackListRepository blackListRepository;

    @InjectMocks AdminBlackMemberServiceImpl adminService;


    @Test
    @DisplayName("이메일 차단 유뮤 테스트")
    void temporarilyUnblockUser(){
        BlackList blackList = BlackList.builder().id(1l).black_check(false).email("test@example.com").build();

        when(blackListRepository.findById(eq(1l))).thenReturn(Optional.of(blackList));
        when(blackListRepository.save(any(BlackList.class))).thenReturn(blackList);

        boolean temporarilied = adminService.temporarilyUnblockUser(1l);

        assertThat(temporarilied).isTrue();
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

    @Test
    @DisplayName("전체 회원수 조회")
    void count_members(){
        long count =10;

        when(memberRepository.countAllBy()).thenReturn(count);

        long l = adminService.searchAllMembers();
        assertThat(l).isEqualTo(count);
    }
}