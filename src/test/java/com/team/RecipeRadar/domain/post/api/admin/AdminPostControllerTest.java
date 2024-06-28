package com.team.RecipeRadar.domain.post.api.admin;

import com.team.RecipeRadar.domain.post.application.admin.AdminPostService;
import com.team.RecipeRadar.domain.blackList.dto.response.PostsCommentResponse;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.application.user.PostServiceImpl;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.response.PostResponse;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.RecipeRadar.domain.email.event.ResignEmailHandler;
import com.team.mock.CustomMockAdmin;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@Import(SecurityTestConfig.class)
@WebMvcTest(AdminPostController.class)
class AdminPostControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean ApplicationEventPublisher eventPublisher;
    @MockBean ResignEmailHandler resignEmailHandler;
    @MockBean ApplicationEvent applicationEvent;
    @MockBean AdminPostService adminService;
    @MockBean PostServiceImpl postService;

    @Test
    @DisplayName("게시글 수 전제 조회")
    @CustomMockAdmin
    void getPosts_count() throws Exception {
        long count =10;
        given(adminService.searchAllPosts()).willReturn(count);

        mockMvc.perform(get("/api/admin/posts/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(count));
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
        given(adminService.getPostsComments(eq(post_id),isNull(),any())).willReturn(postsCommentResponse);

        mockMvc.perform(get("/api/admin/posts/"+post_id+"/comments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data.comment.[0].member.nickname").value("닉네임1"))
                .andExpect(jsonPath("$.data.nextPage").value(false));

    }
    @Test
    @DisplayName("어드민 댓글 일괄 삭제 API 구현")
    @CustomMockAdmin
    void deleteAllComments() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(adminService).deleteComments(anyList());

        mockMvc.perform(delete("/api/admin/posts/comments?")
                        .param("commentIds", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 삭제 성공"));
    }

    @Test
    @DisplayName("어드민 API 요청시 권한이 어드민이 아닌 유저의 대해서 403 테스트")
    @CustomMockUser//일반 사용자 권한으로 접근
    void NoRoles_admin() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(adminService).deleteComments(anyList());

        mockMvc.perform(delete("/api/admin/posts/comments?")
                        .param("commentIds", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andExpect(status().is(401));
    }




    @Test
    @DisplayName("어드민 게시글 일괄 삭제 API 구현")
    @CustomMockAdmin
    void deleteAllPosts() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(adminService).deletePosts(anyList());

        mockMvc.perform(delete("/api/admin/posts")
                        .param("postIds", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 삭제 성공"));
    }

    @Test
    @CustomMockAdmin
    @DisplayName("게시글 검색 API TEST")
    void searchPostData() throws Exception {
        String loginId = "searchId";
        String postTitle = "제목";
        List<PostDto> postDtos = List.of(
                PostDto.builder().postContent("글").postTitle("제목").member(MemberDto.builder().loginId(loginId).nickname("닉네임").build()).recipe(RecipeDto.builder().id(1L).title("레시피제목").build()).build(),
                PostDto.builder().postContent("글1").postTitle("제목1").member(MemberDto.builder().loginId(loginId).nickname("닉네임1").build()).recipe(RecipeDto.builder().id(1L).title("레시피제목1").build()).build()
        );
        PostResponse postResponse = new PostResponse(true, postDtos);

        given(adminService.searchPost(eq(loginId),isNull(), eq(postTitle),isNull(), any())).willReturn(postResponse);

        mockMvc.perform(get("/api/admin/posts/search")
                        .param("loginId",loginId)
                        .param("postTitle",postTitle))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextPage").value(true))
                .andExpect(jsonPath("$.data.posts").isArray())
                .andExpect(jsonPath("$.data.posts[0].postTitle").value("제목"))
                .andExpect(jsonPath("$.data.posts[0].postContent").value("글"))
                .andExpect(jsonPath("$.data.posts[1].member.loginId").value("searchId"))
                .andExpect(jsonPath("$.data.posts[1].recipe.id").value(1))
                .andExpect(jsonPath("$.data.posts[1].recipe.title").value("레시피제목1"));
    }

}