package com.team.RecipeRadar.domain.member.application.admin;

import com.team.RecipeRadar.domain.blackList.dao.BlackListRepository;
import com.team.RecipeRadar.domain.blackList.dto.response.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.application.user.MemberServiceImpl;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminMemberServiceTest {

    @Mock MemberRepository memberRepository;
    @Mock BlackListRepository blackListRepository;
    @Mock MemberServiceImpl memberService;

    @InjectMocks AdminMemberServiceImpl adminMemberService;

    private List<MemberDto> memberDtos;
    private String loginId = "testId1";

    @BeforeEach
    void SetUp(){
        memberDtos = List.of(
                MemberDto.builder().id(1L).username("회원1").email("email1").loginId("testId1").nickname("닉네임1").join_date(LocalDate.now()).build(),
                MemberDto.builder().id(2L).username("회원2").email("email2").loginId("testId2").nickname("닉네임2").join_date(LocalDate.now()).build()
        );
    }

    @Test
    @DisplayName("전체 사용자 무한 페이징")
    public void infinite_page(){
        Pageable pageRequest = PageRequest.of(0, 2);

        SliceImpl<MemberDto> memberSlice = new SliceImpl<>(memberDtos, pageRequest, true);
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

        SliceImpl<MemberDto> memberSlice = new SliceImpl<>(memberDtos, pageRequest, true);
        when(memberRepository.searchMember(eq(loginId),eq("닉네임2"),isNull(),isNull(),isNull(),eq(pageRequest))).thenReturn(memberSlice);

        MemberInfoResponse memberInfoResponse = adminMemberService.searchMember(loginId,"닉네임2",null,null,null,pageRequest);

        assertThat(memberInfoResponse.getNextPage()).isTrue();
        assertThat(memberInfoResponse.getMemberInfoes()).hasSize(2);
        assertThat(memberInfoResponse.getMemberInfoes().get(0).getLoginId()).isEqualTo(loginId);
    }



    @Test
    @DisplayName("관리자가 사용자 삭제 성공")
    void adminDeleteUsers_success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(MemberDto.toEntity(memberDtos.get(0))));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(MemberDto.toEntity(memberDtos.get(1))));

        when(blackListRepository.existsByEmail("email1")).thenReturn(false);
        when(blackListRepository.existsByEmail("email2")).thenReturn(true);

        doNothing().when(memberService).deleteByLoginId(anyString());

        List<String> deletedEmails = adminMemberService.adminDeleteUsers(List.of(1L, 2L));

        verify(memberRepository, times(2)).findById(anyLong());
        verify(blackListRepository, times(1)).save(any());

        verify(memberService, times(2)).deleteByLoginId(anyString());

        assertThat(deletedEmails).containsExactlyInAnyOrder("email1");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 삭제 시 예외 발생")
    void adminDeleteUsers_memberNotFound() {
        when(memberRepository.findById(anyLong())).thenThrow(NoSuchDataException.class);

        assertThatThrownBy(() -> adminMemberService.adminDeleteUsers(List.of(1l)))
                .isInstanceOf(NoSuchDataException.class);
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
