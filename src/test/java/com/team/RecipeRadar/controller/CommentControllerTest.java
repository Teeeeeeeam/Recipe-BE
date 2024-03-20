package com.team.RecipeRadar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.Service.impl.CommentServiceImpl;
import com.team.RecipeRadar.dto.PostDto;
import com.team.RecipeRadar.dto.CommentDto;
import com.team.RecipeRadar.dto.MemberDto;
import com.team.RecipeRadar.filter.jwt.JwtProvider;
import com.team.RecipeRadar.repository.MemberRepository;
import com.team.RecipeRadar.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CommentController.class)
@Slf4j
class CommentControllerTest {


    @MockBean private CommentServiceImpl commentService;
    @Autowired private MockMvc mockMvc;

    @MockBean MemberRepository memberRepository;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @CustomMockUser
    @DisplayName("댓글 작성 Controller 테스트")
    void commnet_add_test() throws Exception {

        given(commentService.save(CommentDto.builder()
                .id(1l)
                .memberDto(MemberDto.builder().id(2l).build())
                .comment_content("테스트 댓글").build())).willReturn(Comment.builder().id(1l).commentContent("테스트 댓글").member(Member.builder().id(2l).build()).build());
        CommentDto commentDto1 = CommentDto.builder()
                .id(1l)
                .memberDto(MemberDto.builder().id(2l).build())
                .comment_content("테스트 댓글").build();
         mockMvc.perform(post("/api/user/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto1)))
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("댓글 등록성공"))
                .andExpect(status().isOk());

    }


    @Test
    @CustomMockUser
    @DisplayName("댓글 삭제 테스트 - 댓글이 존재하는 경우")
    void comment_delete_existing_comment_test() throws Exception {
        // given
        MemberDto memberDto = MemberDto.builder().id(2l).build();
        CommentDto commentDto = CommentDto.builder().id(1l).memberDto(memberDto).comment_content("테스트 댓글").build();

        Member member = Member.builder().id(2l).build();
        Comment comment = Comment.builder().commentContent("테스트 댓글").id(1l).member(member).build();
        doNothing().when(commentService).delete_comment(commentDto);
//        given(commentService.delete_comment(commentDto)).willReturn(comment);

        // when, then
        mockMvc.perform(delete("/api/user/comment/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @CustomMockUser
    @DisplayName("게시글의 모든 댓글 출력")
    void comment_getAll() throws Exception {

        //게시글 아이디
        long postId = Long.parseLong("55");

        MemberDto memberDto = MemberDto.builder().id(1L).build();
        PostDto articleDto = PostDto.builder().id(postId).build();

        //페이징 테스트를 위한 객체 생성
        List<CommentDto> commentDtos = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            CommentDto commentDto = CommentDto.builder()
                    .id((long) i)
                    .comment_content("테스트 댓글 내용 " + i)
                    .memberDto(memberDto)
                    .articleDto(articleDto)
                    .build();
            commentDtos.add(commentDto);
        }
        
        //페이징 객체 직접 생성
        Page<CommentDto> page = new PageImpl<>(commentDtos);

        // commentService.commentPage() 메서드의 게시글 id가 55일때 page 객체 반환
        given(commentService.commentPage(eq(postId), any(Pageable.class))).willReturn(page);

        // GET 요청 수행 및 응답 확인
        mockMvc.perform(get("/api/comment")
                        .param("posts", "55")
                        .param("page","0")
                        .param("size","5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.*",hasSize(10)))
                .andExpect(jsonPath("$.[0].id",is(1)))
                .andDo(print()); // 결과를 콘솔에 출력하여 확인

        //게시글이 존재하지않았을때 false로 값이 나오는지 확인
        mockMvc.perform(get("/api/comment")
                        .param("posts", "123")
                        .param("page","1")
                        .param("size","5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500)) // 상태 코드 500 확인
                .andExpect(jsonPath("$.*",hasSize(2)))
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print()); // 결과를 콘솔에 출력하여 확인
    }

    @Test
    @CustomMockUser
    void test() throws Exception {
        String update_content = "변경된 댓글";
        // Member 객체의 ID를 정확히 설정
        Member member = Member.builder().id(2L).build();
        Comment comment = Comment.builder().id(1L).commentContent("변경전").member(member).build();

        // MemberDto 객체를 생성할 때 Member 객체의 ID를 사용
        MemberDto memberDto = MemberDto.builder().id(member.getId()).build();
        CommentDto commentDto = CommentDto.builder().id(comment.getId()).memberDto(memberDto).comment_content(comment.getCommentContent()).build();

        // CommentService의 update 메서드를 호출할 때 memberDto.getId()가 null이 아니도록 함
        doNothing().when(commentService).update(member.getId(), commentDto.getId(), update_content);

        log.info("comment={}", comment.getCommentContent());
        mockMvc.perform(put("/api/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
