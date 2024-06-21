package com.team.RecipeRadar.domain.notice.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.notice.application.admin.AdminNoticeService;
import com.team.RecipeRadar.domain.notice.dto.request.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.request.AdminUpdateRequest;
import com.team.RecipeRadar.global.conig.TestConfig;
import com.team.mock.CustomMockAdmin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestConfig.class)
@WebMvcTest(AdminNoticeController.class)
class AdminNoticeControllerTest {


    @MockBean AdminNoticeService adminNoticeService;
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
        doNothing().when(adminNoticeService).save(eq(adminAddRequest),anyLong(), any(MockMultipartFile.class));

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

        doNothing().when(adminNoticeService).delete(anyList());

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
        doNothing().when(adminNoticeService).update(anyLong(),eq(adminUpdateRequest), any(MockMultipartFile.class));
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

    private MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile("사진", originalName, "image/jpeg", "Test".getBytes());
    }
}