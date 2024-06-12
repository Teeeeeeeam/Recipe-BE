package com.team.RecipeRadar.domain.comment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.comment.api.CommentController;
import com.team.RecipeRadar.domain.comment.application.CommentServiceImpl;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.user.UserAddCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.user.UserDeleteCommentRequest;
import com.team.RecipeRadar.domain.comment.dto.user.UserUpdateCommentRequest;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

        UserAddCommentRequest commentDto = UserAddCommentRequest.builder().memberId(2l).postId(3l).commentContent("테스트 댓글").build();

        given(commentService.save(commentDto))
                .willReturn(Comment.builder().id(1l).commentContent("테스트 댓글").member(Member.builder().id(2l).build()).post(Post.builder().id(3l).build()).build());

        mockMvc.perform(post("/api/user/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("성공"))
                .andExpect(jsonPath("$.data.commentContent").value("테스트 댓글"))
                .andExpect(jsonPath("$.data.memberId").value(2))
                .andExpect(jsonPath("$.data.postId").value(3))
                .andExpect(jsonPath("$.data.created_at").doesNotExist())
                .andExpect(status().isOk());

    }


    @Test
    @CustomMockUser
    @DisplayName("댓글 삭제 테스트 - 댓글이 존재하는 경우")
    void comment_delete_existing_comment_test() throws Exception {
        // given
        String nickName="testNickName";
        UserDeleteCommentRequest userDeleteCommentDto = new UserDeleteCommentRequest(2l, 1l);
        CommentDto commentDto = CommentDto.builder().id(1l).nickName(nickName).comment_content("테스트 댓글").build();

        Member member = Member.builder().id(2l).build();
        Comment comment = Comment.builder().commentContent("테스트 댓글").id(1l).member(member).build();
        doNothing().when(commentService).delete_comment(userDeleteCommentDto);
//        given(commentService.delete_comment(commentDto)).willReturn(comment);

        // when, then
        mockMvc.perform(delete("/api/user/comments")
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

        String nickName= "testNickName";
        PostDto articleDto = PostDto.builder().id(postId).build();

        //페이징 테스트를 위한 객체 생성
        List<CommentDto> commentDtos = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            CommentDto commentDto = CommentDto.builder()
                    .id((long) i)
                    .comment_content("테스트 댓글 내용 " + i)
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
                        .param("posts", "55")
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
        String update_content = "변경된 댓글";
        Long memberId = 2L;
        Long commentId = 1L;

        doNothing().when(commentService).update(memberId, commentId, update_content);

        Comment updatedComment = Comment.builder()
                .id(commentId)
                .commentContent(update_content)
                .member(Member.builder().id(memberId).build())
                .updated_at(LocalDateTime.now())
                .build();
        given(commentService.findById(commentId)).willReturn(updatedComment);

        UserUpdateCommentRequest updateCommentDto = UserUpdateCommentRequest.builder()
                .commentContent(update_content)
                .commentId(commentId)
                .memberId(memberId)
                .build();

        mockMvc.perform(put("/api/user/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.commentContent").value(update_content))
                .andExpect(jsonPath("$.data.memberId").value(memberId))
                .andExpect(jsonPath("$.data.commentId").value(commentId))
                .andExpect(jsonPath("$.data.updateAt").exists())
                .andDo(print());
    }

}
