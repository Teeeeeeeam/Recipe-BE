package com.team.RecipeRadar.domain.notice.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.application.user.NoticeService;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.response.InfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.response.InfoNoticeResponse;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityTestConfig.class)
@WebMvcTest(NoticeController.class)
class NoticeControllerTest {


    @MockBean NoticeService noticeService;
    @Autowired MockMvc mockMvc;

    @Test
    @DisplayName("메인 공지사항")
    void mainNotice() throws Exception {
        List<NoticeDto> mockNoticeDtoList = createMockNoticeDtoList();

        given(noticeService.mainNotice()).willReturn(mockNoticeDtoList);

        mockMvc.perform(get("/api/notice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(5))
                .andDo(print());
    }

    @Test
    @DisplayName("공지사항 무한 페이징")
    void adminNotice() throws Exception {

        InfoNoticeResponse infoNoticeResponse = new InfoNoticeResponse(false, createMockNoticeDtoList());
        given(noticeService.noticeInfo(isNull(),any(Pageable.class))).willReturn(infoNoticeResponse);
        
        mockMvc.perform(get("/api/notices?size=3"))
                .andExpect(jsonPath("$.data.nextPage").value(false))
                .andExpect(jsonPath("$.data.notice.size()").value(5))
                .andDo(print());
    }

    @Test
    @DisplayName("공지사항 상제 페이지")
    void adminDetailNotice() throws Exception {
        Member member = Member.builder().id(1l).nickName("닉네임").loginId("로그인아이디").build();
        InfoDetailsResponse infoDetailsResponse = InfoDetailsResponse.builder().noticeTitle("제목").noticeContent("내용")
                .createdAt(LocalDateTime.now().toLocalDate()).imgUrl("https://www.example.com/image").member(MemberDto.from(member)).build();

        given(noticeService.detailNotice(anyLong())).willReturn(infoDetailsResponse);

        mockMvc.perform(get("/api/notices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.member.nickname").value("닉네임"))
                .andDo(print());
    }
    public static List<NoticeDto> createMockNoticeDtoList() {
        List<NoticeDto> mockList = new ArrayList<>();
        Member member = Member.builder().id(1l).nickName("닉네임").loginId("로그인아이디").build();
        for (long i = 1; i <= 5; i++) {
            mockList.add(
                    NoticeDto.builder()
                            .id(i)
                            .noticeTitle("제목" + i)
                            .member(MemberDto.from(member))
                            .createdAt(LocalDateTime.now().toLocalDate())
                            .imgUrl("https://www.example.com/image" + i + ".jpg")
                            .build()
            );
        }
        return mockList;
    }
}