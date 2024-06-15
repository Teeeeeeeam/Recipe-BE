package com.team.RecipeRadar.domain.notice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.application.NoticeService;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminUpdateRequest;
import com.team.RecipeRadar.domain.notice.dto.info.InfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.info.InfoNoticeResponse;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockAdmin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(NoticeController.class)
class NoticeControllerTest {


    @MockBean NoticeService noticeService;
    @MockBean MemberRepository memberRepository;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;
    @Autowired MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    private final String originalName = "test.png";


    @Test
    @DisplayName("공지사항 작성")
    @CustomMockAdmin
    void noticeAdd() throws Exception {
        AdminAddRequest adminAddRequest = new AdminAddRequest();
        adminAddRequest.setNoticeTitle("제목");
        adminAddRequest.setNoticeContent("내용");

        MockMultipartFile file = getMockMultipartFile();
        doNothing().when(noticeService).save(eq(adminAddRequest),anyLong(), any(MockMultipartFile.class));

        MockMultipartFile request = new MockMultipartFile("adminAddRequest", null, "application/json", objectMapper.writeValueAsString(adminAddRequest).getBytes(StandardCharsets.UTF_8));


        mockMvc.perform(multipart("/api/admin/notices")
                .file(request)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("작성 성공"));
    }

    @Test
    @DisplayName("공지사항 삭제")
    @CustomMockAdmin
    void deleteNotice() throws Exception {

        doNothing().when(noticeService).delete(anyList());

        mockMvc.perform(delete("/api/admin/notices")
                .param("noticeIds","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("공지사항 삭제 성공"));
    }

    @Test
    @DisplayName("공지사항 업데이트")
    @CustomMockAdmin
    void updateNotice() throws Exception{

        AdminUpdateRequest adminUpdateRequest = new AdminUpdateRequest();
        adminUpdateRequest.setNoticeTitle("제목");
        adminUpdateRequest.setNoticeContent("내용");

        MockMultipartFile file = getMockMultipartFile();
        doNothing().when(noticeService).update(anyLong(),eq(adminUpdateRequest), any(MockMultipartFile.class));
        MockMultipartFile updateRequest = new MockMultipartFile("adminUpdateRequest", null, "application/json", objectMapper.writeValueAsString(adminUpdateRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/admin/notices/1")
                        .file(updateRequest)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("공지사항 수정 성공"));

    }

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
                .createdAt(LocalDateTime.now()).imgUrl("https://www.example.com/image").member(MemberDto.from(member)).build();

        given(noticeService.detailNotice(anyLong())).willReturn(infoDetailsResponse);

        mockMvc.perform(get("/api/notices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.member.nickname").value("닉네임"))
                .andDo(print());
    }

    private MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile("사진", originalName, "image/jpeg", "Test".getBytes());
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
                            .createdAt(LocalDateTime.now())
                            .imgUrl("https://www.example.com/image" + i + ".jpg")
                            .build()
            );
        }
        return mockList;
    }
}