package com.team.RecipeRadar.domain.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.application.PostServiceImpl;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.dto.user.*;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @MockBean
    private PostServiceImpl postService;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MemberRepository memberRepository;
    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    CustomOauth2Handler customOauth2Handler;
    @MockBean
    CustomOauth2Service customOauth2Service;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("사용자가 작성한 게시글 목록 조회 API 성공 테스트")
    @CustomMockUser
    void postTitlePage_success() throws Exception {

        Cookie cookie = new Cookie("login-id", "fakeCookie");
        String loginId= "test";

        List<UserInfoPostRequest > requests = new ArrayList<>();
        requests.add(new UserInfoPostRequest(2l,"타이틀1"));
        requests.add(new UserInfoPostRequest(3l,"타이틀2"));

        UserInfoPostResponse infoPostResponse = UserInfoPostResponse.builder()
                .nextPage(false)
                .content(requests).build();

        given(postService.userPostPage(anyString(),anyString(),any(Pageable.class))).willReturn(infoPostResponse);

        mockMvc.perform(get("/api/user/info/{login-id}/posts",loginId)
                .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data.nextPage").value(false))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content.[0].id").value(2))
                .andExpect(jsonPath("$.data.content.[0].postTitle").value("타이틀1"))
                .andExpect(jsonPath("$.data.content.[1].id").value(3))
                .andExpect(jsonPath("$.data.content.[1].postTitle").value("타이틀2"));


    }

    @Test
    @DisplayName("사용자가 작성한 게시글 목록 조회 API 실패 테스트")
    @CustomMockUser
    void postTitlePage_fail() throws Exception {

        String loginId= "test";
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        given(postService.userPostPage(anyString(),anyString(),any(Pageable.class))).willThrow(new AccessDeniedException("접근 할수 없는 페이지 입니다."));

        mockMvc.perform(get("/api/user/info/{login-id}/posts",loginId)
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("접근 할수 없는 페이지 입니다."));
    }
    
    @Test
    @DisplayName("사용자 게시글 등록 API 테스트")
    @CustomMockUser
    void save_postAPI() throws Exception {
        String file = "test.jpg";
        UserAddRequest userAddRequest = new UserAddRequest();
        userAddRequest.setPostContent("컨텐트");
        userAddRequest.setPostServing("인원");
        userAddRequest.setPostTitle("제목");
        userAddRequest.setPostCookingLevel("level");
        userAddRequest.setPostCookingTime("cookingTime");
        userAddRequest.setRecipe_id(1L);
        userAddRequest.setMemberId(2L);
        userAddRequest.setPostPassword("1234");

        MockMultipartFile multipartFile = new MockMultipartFile("file", file, "image", "test data".getBytes());

        MockMultipartFile userAddRequest1 = new MockMultipartFile("userAddPostDto", null, "application/json", objectMapper.writeValueAsString(userAddRequest).getBytes(StandardCharsets.UTF_8));
        doNothing().when(postService).save(eq(userAddRequest),eq(multipartFile));

        mockMvc.perform(multipart("/api/user/posts")
                        .file(multipartFile)
                        .file(userAddRequest1)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("작성 성공"));
    }

    @Test
    @DisplayName("사용자 게시글 등록 API @Valid 테스트")
    @CustomMockUser
    void save_postAPI_Valid() throws Exception {
        String file = "test.jpg";
        UserAddRequest userAddRequest = new UserAddRequest();
        userAddRequest.setPostContent("컨텐트");
        userAddRequest.setPostTitle("제목");
        userAddRequest.setPostCookingTime("cookingTime");
        userAddRequest.setRecipe_id(1L);
        userAddRequest.setMemberId(2L);
        userAddRequest.setPostPassword("1234");

        MockMultipartFile multipartFile = new MockMultipartFile("file", file, "image", "test data".getBytes());

        MockMultipartFile userAddRequest1 = new MockMultipartFile("userAddPostDto", null, "application/json", objectMapper.writeValueAsString(userAddRequest).getBytes(StandardCharsets.UTF_8));
        doNothing().when(postService).save(eq(userAddRequest),eq(multipartFile));

        mockMvc.perform(multipart("/api/user/posts")
                        .file(multipartFile)
                        .file(userAddRequest1)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("모든 값을 입력해 주세요"))
                .andExpect(jsonPath("$.data.size()").value(2));     //두개의 valid 발생
    }
    
    @Test
    @DisplayName("게시글 조회 API 무한 페이징 테스트")
    void paging_API() throws Exception {
        Pageable pageRequest = PageRequest.of(0, 2);

        List<PostDto> postDtos = List.of(PostDto.builder().postContent("컨텐트").id(1l).build(), PostDto.builder().postContent("컨텐트2").id(2l).build());
        PostResponse postResponse = new PostResponse(false, postDtos);
        given(postService.postPage(eq(pageRequest))).willReturn(postResponse);

        mockMvc.perform(get("/api/posts?size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextPage").value(false))
                .andExpect(jsonPath("$.posts.[0].id").value(1))
                .andExpect(jsonPath("$.posts.[1].postContent").value("컨텐트2"))
                .andExpect(jsonPath("$.posts.size()").value(2));
    }
    
    @Test
    @DisplayName("게시글 상세 조회")
    @CustomMockUser
    void details_posts() throws Exception {
        Long postId= 1l;
        List<CommentDto> commentDtoListbuild = List.of(CommentDto.builder().id(1l).comment_content("댓글1").build(), CommentDto.builder().id(2l).comment_content("댓글12").build());
        PostDto postDto = PostDto.builder().id(postId).postContent("컨텐트").postTitle("제목").postCookingLevel("레밸").build();
        PostDetailResponse postDetailResponse = new PostDetailResponse(postDto, commentDtoListbuild);
        given(postService.postDetail(anyLong())).willReturn(postDetailResponse);

        mockMvc.perform(get("/api/user/posts/"+postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("조회성공"))
                .andExpect(jsonPath("$.data.post.postTitle").value("제목"))
                .andExpect(jsonPath("$.data.comments.size()").value(2));

    }

    @Test
    @DisplayName("게시글 조회시 데이터 없을 때 예외 테스트")
    @CustomMockUser
    void details_posts_empty() throws Exception {
        given(postService.postDetail(anyLong())).willThrow(new BadRequestException("게시글이 존재하지 않습니다."));

        mockMvc.perform(get("/api/user/posts/1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    @CustomMockUser
    void update_posts() throws Exception {
        Long postId = 1L;
        String loginId = "testId";
        String password = "1234";

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setPostId(postId);
        userUpdateRequest.setPostTitle("새로운 제목");
        userUpdateRequest.setPostContent("새로운 내용");
        userUpdateRequest.setPostServing("4인분");
        userUpdateRequest.setPostCookingTime("45분");
        userUpdateRequest.setPostCookingLevel("중간");
        userUpdateRequest.setPostImageUrl("새로운 이미지 URL");
        userUpdateRequest.setPostPassword(password);
        doNothing().when(postService).update(userUpdateRequest,loginId);

        mockMvc.perform(put("/api/user/posts")
                .content(objectMapper.writeValueAsString(userUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요리글 수정 성공"));
    }

    @Test
    @DisplayName("게시글 수정 @Valid 테스트")
    @CustomMockUser
    void update_posts_valid() throws Exception {
        Long postId = 1L;
        String loginId = "testId";
        String password = "1234";

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setPostId(postId);
        userUpdateRequest.setPostTitle("새로운 제목");
        userUpdateRequest.setPostImageUrl("새로운 이미지 URL");
        userUpdateRequest.setPostPassword(password);
        doNothing().when(postService).update(userUpdateRequest,loginId);

        mockMvc.perform(put("/api/user/posts")
                        .content(objectMapper.writeValueAsString(userUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("모든 값을 입력해 주세요"))
                .andExpect(jsonPath("$.data.size()").value("4"));
    }
    
    @Test
    @DisplayName("게시글 삭제 API 테스트")
    @CustomMockUser
    void delete_posts() throws Exception {
        Long postId = 1l;
        doNothing().when(postService).delete(anyString(),anyLong());

        mockMvc.perform(delete("/api/user/posts/"+postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 삭제 성공"));
    }

    @Test
    @DisplayName("게시글 작성자만 삭제 가능 테스트")
    @CustomMockUser
    void delete_posts_member() throws Exception {
        Long postId = 1l;
        doThrow(new AccessDeniedException("작성자만 삭제할수 있습니다."))
                .when(postService).delete(anyString(), anyLong());

        mockMvc.perform(delete("/api/user/posts/"+postId))
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("작성자만 삭제할수 있습니다."));
    }

    @Test
    @DisplayName("게시글 비밀번호 유효성 검사 불일치")
    @CustomMockUser
    void validPost_invalidPassword_throwsBadRequestException() throws Exception {
        doThrow(new BadRequestException("비밀번호가 일치하지 않습니다."))
                .when(postService).validPostPassword(anyString(), any());

        ValidPostRequest validPostRequest = new ValidPostRequest();
        validPostRequest.setPassword("1234");
        validPostRequest.setPostId(1l);

        mockMvc.perform(post("/api/valid/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("게시글 비밀번호 유효성 검사")
    @CustomMockUser
    void validPost_invalidPassword() throws Exception {

        String loginId = "testId";
        Long postId = 1l;

        ValidPostRequest validPostRequest = new ValidPostRequest();
        validPostRequest.setPassword("1234");
        validPostRequest.setPostId(postId);

        given(postService.validPostPassword(eq(loginId),eq(validPostRequest))).willReturn(true);

        mockMvc.perform(post("/api/valid/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPostRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호 인증 성공"));
    }
}