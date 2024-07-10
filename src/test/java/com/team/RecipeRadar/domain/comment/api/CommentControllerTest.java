package com.team.RecipeRadar.domain.comment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.blackList.dto.response.PostsCommentResponse;
import com.team.RecipeRadar.domain.comment.application.CommentServiceImpl;
import com.team.RecipeRadar.domain.comment.dto.request.UserAddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.request.UserDeleteCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.request.UserUpdateCommentRequest;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.mock.CustomMockAdmin;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

        mockMvc.perform(delete("/api/user/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDeleteCommentDto)))
                .andExpect(status().isOk())
                .andDo(print());
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

    @Test
    @DisplayName("어드민 게시글 관련 댓글 조회")
    @CustomMockAdmin
    void postsContainsComment() throws Exception {
        Long post_id= 1l;
        List<CommentDto> commentDtoList = List.of(
                CommentDto.builder().commentContent("댓글1").member(MemberDto.builder().loginId("testId").username("실명1").nickname("닉네임1").build()).build(),
                CommentDto.builder().commentContent("댓글2").member(MemberDto.builder().loginId("testId1").username("실명2").nickname("닉네임2").build()).build()
        );
        PostsCommentResponse postsCommentResponse = new PostsCommentResponse(false, commentDtoList);
        given(commentService.getPostsComments(eq(post_id),isNull(),any())).willReturn(postsCommentResponse);

        mockMvc.perform(get("/api/user/posts/"+post_id+"/comments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data.comment.[0].member.nickname").value("닉네임1"))
                .andExpect(jsonPath("$.data.nextPage").value(false));

    }
}
