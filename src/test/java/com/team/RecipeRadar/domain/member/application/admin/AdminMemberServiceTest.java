package com.team.RecipeRadar.domain.member.application.admin;

import com.team.RecipeRadar.domain.blackList.dao.BlackListRepository;
import com.team.RecipeRadar.domain.blackList.dto.response.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.application.user.MemberServiceTest;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminMemberServiceTest {

    @Mock MemberRepository memberRepository;
    @Mock BlackListRepository blackListRepository;
    @Mock
    MemberServiceTest memberService;

    @InjectMocks AdminMemberServiceImpl adminMemberService;


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

        MemberInfoResponse memberInfoResponse = adminMemberService.memberInfos(1l,pageRequest);

        assertThat(memberInfoResponse.getNextPage()).isTrue();
        assertThat(memberInfoResponse.getMemberInfoes()).hasSize(2);
        assertThat(memberInfoResponse.getMemberInfoes().get(0).getLoginId()).isEqualTo(loginId);
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

        MemberInfoResponse memberInfoResponse = adminMemberService.searchMember(loginId,"닉네임2",null,null,null,pageRequest);

        assertThat(memberInfoResponse.getNextPage()).isTrue();
        assertThat(memberInfoResponse.getMemberInfoes()).hasSize(2);
        assertThat(memberInfoResponse.getMemberInfoes().get(0).getLoginId()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("전체 회원수 조회")
    void count_members(){
        long count =10;

        when(memberRepository.countAllBy()).thenReturn(count);

        long l = adminMemberService.searchAllMembers();
        assertThat(l).isEqualTo(count);
    }
}
