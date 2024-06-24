package com.team.RecipeRadar.domain.comment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.comment.application.CommentServiceImpl;
import com.team.RecipeRadar.domain.comment.dto.request.UserAddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.request.UserDeleteCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.request.UserUpdateCommentRequest;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityTestConfig.class)
@WebMvcTest(CommentController.class)
class CommentControllerTest {


    @MockBean CommentServiceImpl commentService;
    @Autowired MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @CustomMockUser
    @DisplayName("댓글 작성 Controller 테스트")
    void commnet_add_test() throws Exception {


        UserAddCommentRequest userAddCommentRequest = new UserAddCommentRequest();
        userAddCommentRequest.setPostId(1l);
        userAddCommentRequest.setCommentContent("테스트 댓글");

        doNothing().when(commentService).save(anyLong(),anyString(),anyLong());

        mockMvc.perform(post("/api/user/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAddCommentRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("댓글 작성 성공"))
                .andExpect(status().isOk());

    }


    @Test
    @CustomMockUser
    @DisplayName("댓글 삭제 테스트 - 댓글이 존재하는 경우")
    void comment_delete_existing_comment_test() throws Exception {
        UserDeleteCommentRequest userDeleteCommentDto = new UserDeleteCommentRequest(2l);

        doNothing().when(commentService).deleteComment(anyLong(),anyLong());

        // when, then
        mockMvc.perform(delete("/api/user/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDeleteCommentDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @CustomMockUser
    @DisplayName("게시글의 모든 댓글 출력")
    void comment_getAll() throws Exception {

        //게시글 아이디
        long postId = Long.parseLong("55");

        String nickName= "testNickName";
        PostDto articleDto = PostDto.builder().id(postId).build();

        //페이징 테스트를 위한 객체 생성
        List<CommentDto> commentDtos = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            CommentDto commentDto = CommentDto.builder()
                    .id((long) i)
                    .commentContent("테스트 댓글 내용 " + i)
                    .nickName(nickName)
                    .articleDto(articleDto)
                    .build();
            commentDtos.add(commentDto);
        }
        //페이징 객체 직접 생성
        Page<CommentDto> page = new PageImpl<>(commentDtos);

        // commentService.commentPage() 메서드의 게시글 id가 55일때 page 객체 반환
        given(commentService.commentPage(eq(postId), any(Pageable.class))).willReturn(page);


        // GET 요청 수행 및 응답 확인
        mockMvc.perform(get("/api/comments")
                        .param("postId", "55")
                        .param("page","0")
                        .param("size","5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 상태 코드 200 확인
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(10))
                .andDo(print()); // 결과를 콘솔에 출력하여 확인
    }

    @Test
    @CustomMockUser
    @DisplayName("댓글 업데이트")
    void update_comment() throws Exception {
        Long memberId = 1l;
        Long commentId = 1l;

        UserUpdateCommentRequest userUpdateCommentRequest = new UserUpdateCommentRequest();
        userUpdateCommentRequest.setCommentContent("변경된 댓글");
        userUpdateCommentRequest.setCommentId(commentId);

        doNothing().when(commentService).update(anyLong(),anyString(), eq(memberId));

        mockMvc.perform(put("/api/user/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateCommentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());
    }

}
