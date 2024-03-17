package com.team.RecipeRadar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.Service.CommentService;
import com.team.RecipeRadar.dto.CommentDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CommentControllerTest {

    @Mock private CommentService commentService;
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }
    @Test
    @DisplayName("댓글 작성 Controller 테스트")
    void commnet_add_test() throws Exception {
        CommentDto commentDto = CommentDto.builder().comment_content("테스트용 댓글").build();

        LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);

        when(commentService.save(any(CommentDto.class))).thenAnswer(invocation -> {
            CommentDto dto = invocation.getArgument(0);
            // 실제로 DB에 저장된 Comment 객체를 생성하고 필요한 필드 설정
            Comment savedComment = new Comment();
            savedComment.setId(1l); // 예시: ID 설정
            savedComment.setComment_content(dto.getComment_content()); // 예시: Comment 내용 설정
            savedComment.setCreated_at(localDateTime); // 예시: 생성 시간 설정
            // 실제 DB에 저장되는 로직을 구현해야 합니다.
            return savedComment;
        });

        MockHttpServletResponse response = mockMvc.perform(post("/api/user/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message.id", greaterThan(0)))
                .andExpect(jsonPath("$.message.comment_content").value("테스트용 댓글"))
//                .andExpect(jsonPath("$.message.created_at").value(localDateTime))
                .andExpect(jsonPath("$.message.updated_at").doesNotExist()) // updated_at 필드는 존재하지 않아야 함
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Integer read = JsonPath.parse(response.getContentAsString()).read("$.message.id");
        Assertions.assertThat(read).isNotNull();
        log.info("read={}",read);

    }

}