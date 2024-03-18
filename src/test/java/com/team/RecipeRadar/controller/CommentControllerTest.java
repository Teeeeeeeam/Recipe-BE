package com.team.RecipeRadar.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.team.RecipeRadar.Entity.Article;
import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.Service.CommentService;
import com.team.RecipeRadar.Service.impl.CommentServiceImpl;
import com.team.RecipeRadar.dto.CommentDto;
import com.team.RecipeRadar.dto.MemberDto;
import com.team.RecipeRadar.exception.ex.CommentException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CommentControllerTest {

    @Mock private CommentServiceImpl commentService;
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
//        CommentDto commentDto = CommentDto.builder().comment_content("테스트용 댓글").build();

        given(commentService.save(CommentDto.builder()
                .id(1l)
                .memberDto(MemberDto.builder().id(2l).build())
                .comment_content("테스트 댓글").build())).willReturn(Comment.builder().id(1l).comment_content("테스트 댓글").member(Member.builder().id(2l).build()).build());
        CommentDto commentDto1 = CommentDto.builder()
                .id(1l)
                .memberDto(MemberDto.builder().id(2l).build())
                .comment_content("테스트 댓글").build();


        MockHttpServletResponse response = mockMvc.perform(post("/api/user/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto1)))
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message.id", greaterThan(0)))
                .andExpect(jsonPath("$.message.comment_content").value("테스트 댓글"))
                .andExpect(jsonPath("$.message.updated_at").doesNotExist()) // updated_at 필드는 존재하지 않아야 함
                .andExpect(status().isOk())
                .andReturn().getResponse();

        Integer read = JsonPath.parse(response.getContentAsString()).read("$.message.id");
        Assertions.assertThat(read).isNotNull();
    }


    @Test
    @DisplayName("댓글 삭제 테스트 - 댓글이 존재하는 경우")
    void comment_delete_existing_comment_test() throws Exception {
        // given
        MemberDto memberDto = MemberDto.builder().id(2l).build();
        CommentDto commentDto = CommentDto.builder().id(1l).memberDto(memberDto).comment_content("테스트 댓글").build();

        Member member = Member.builder().id(2l).build();
        Comment comment = Comment.builder().comment_content("테스트 댓글").id(1l).member(member).build();
        doNothing().when(commentService).delete_comment(commentDto);
//        given(commentService.delete_comment(commentDto)).willReturn(comment);

        // when, then
        mockMvc.perform(delete("/api/user/comment/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

}
