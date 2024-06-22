package com.team.RecipeRadar.domain.post.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.post.application.user.PostServiceImpl;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.dto.user.*;
import com.team.RecipeRadar.global.conig.TestConfig;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestConfig.class)
@WebMvcTest(PostController.class)
class PostControllerTest {

    @MockBean PostServiceImpl postService;
    @Autowired MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("사용자가 작성한 게시글 목록 조회 API 성공 테스트")
    @CustomMockUser
    void postTitlePage_success() throws Exception {

        Cookie cookie = new Cookie("login-id", "fakeCookie");

        List<UserInfoPostRequest > requests = new ArrayList<>();
        requests.add(new UserInfoPostRequest(2l,"타이틀1"));
        requests.add(new UserInfoPostRequest(3l,"타이틀2"));

        UserInfoPostResponse infoPostResponse = new UserInfoPostResponse(false,requests);

        given(postService.userPostPage(anyLong(),isNull(),any(Pageable.class))).willReturn(infoPostResponse);

        mockMvc.perform(get("/api/user/info/posts")
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

        given(postService.userPostPage(anyLong(),isNull(),any(Pageable.class))).willThrow(new IllegalArgumentException("접근 할수 없는 페이지 입니다."));

        mockMvc.perform(get("/api/user/info/posts",loginId)
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andDo(print())
                .andExpect(status().is(403))
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
        userAddRequest.setPostPassword("1234");

        MockMultipartFile multipartFile = new MockMultipartFile("file", file, "image", "test data".getBytes());

        MockMultipartFile userAddRequest1 = new MockMultipartFile("userAddPostRequest", null, "application/json", objectMapper.writeValueAsString(userAddRequest).getBytes(StandardCharsets.UTF_8));


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
    @Disabled
    @DisplayName("사용자 게시글 등록 API @Valid 테스트")
    @CustomMockUser
    void save_postAPI_Valid() throws Exception {
        String file = "test.jpg";
        UserAddRequest userAddRequest = new UserAddRequest();
        userAddRequest.setPostContent("컨텐트");
        userAddRequest.setPostTitle("제목");
        userAddRequest.setPostCookingTime("cookingTime");
        userAddRequest.setPostPassword("1234");

        MockMultipartFile multipartFile = new MockMultipartFile("file", file, "image", "test data".getBytes());

        MockMultipartFile userAddRequest1 = new MockMultipartFile("userAddPostRequest", null, "application/json", objectMapper.writeValueAsString(userAddRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/user/posts")
                        .file(multipartFile)
                        .file(userAddRequest1)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("실패"))
                .andExpect(jsonPath("$.data.size()").value(2));     //두개의 valid 발생
    }
    
    @Test
    @DisplayName("게시글 조회 API 무한 페이징 테스트")
    void paging_API() throws Exception {
        Pageable pageRequest = PageRequest.of(0, 2);

        List<PostDto> postDtos = List.of(PostDto.builder().postContent("컨텐트").id(1l).build(), PostDto.builder().postContent("컨텐트2").id(2l).build());
        PostResponse postResponse = new PostResponse(false, postDtos);
        given(postService.postPage(any(),eq(pageRequest))).willReturn(postResponse);

        mockMvc.perform(get("/api/posts?size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextPage").value(false))
                .andExpect(jsonPath("$.data.posts.[0].id").value(1))
                .andExpect(jsonPath("$.data.posts.[1].postContent").value("컨텐트2"))
                .andExpect(jsonPath("$.data.posts.size()").value(2));
    }
    
    @Test
    @DisplayName("게시글 상세 조회")
    @CustomMockUser
    void details_posts() throws Exception {
        Long postId= 1l;
        List<CommentDto> commentDtoListbuild = List.of(CommentDto.builder().id(1l).commentContent("댓글1").build(), CommentDto.builder().id(2l).commentContent("댓글12").build());
        PostDto postDto = PostDto.builder().id(postId).postContent("컨텐트").postTitle("제목").postCookingLevel("레밸").comments(commentDtoListbuild).build();
        PostDetailResponse postDetailResponse = new PostDetailResponse(postDto);
        given(postService.postDetail(anyLong())).willReturn(postDetailResponse);

        mockMvc.perform(get("/api/user/posts/" + postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("조회성공"))
                .andExpect(jsonPath("$.data.post.postTitle").value("제목"))  // Adjusted JSON path
                .andExpect(jsonPath("$.data.post.comments").isArray());
    }

    @Test
    @DisplayName("게시글 조회시 데이터 없을 때 예외 테스트")
    @CustomMockUser
    void details_posts_empty() throws Exception {
        given(postService.postDetail(anyLong())).willThrow(new NoSuchDataException(NoSuchErrorType.NO_SUCH_POST));

        mockMvc.perform(get("/api/user/posts/1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    @CustomMockUser
    void update_posts() throws Exception {
        Long postId = 1L;
        String loginId = "testId";
        String password = "1234";
        String file = "Test";


        UserUpdateRequest userUpdateRequest_1 = new UserUpdateRequest();
        userUpdateRequest_1.setPostTitle("새로운 제목");
        userUpdateRequest_1.setPostContent("새로운 내용");
        userUpdateRequest_1.setPostServing("4인분");
        userUpdateRequest_1.setPostCookingTime("45분");
        userUpdateRequest_1.setPostCookingLevel("중간");
        userUpdateRequest_1.setPostPassword(password);

        MockMultipartFile multipartFile = new MockMultipartFile("file", file, "image", "test data".getBytes());
        MockMultipartFile userUpdateRequest = new MockMultipartFile("userUpdateRequest", null, "application/json", objectMapper.writeValueAsString(userUpdateRequest_1).getBytes(StandardCharsets.UTF_8));
        doNothing().when(postService).update(postId,1l,userUpdateRequest_1,multipartFile);

        mockMvc.perform(multipart("/api/user/update/posts/"+postId)
                        .file(multipartFile)
                        .file(userUpdateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요리글 수정 성공"));
    }

    @Test
    @Disabled
    @DisplayName("게시글 수정 @Valid 테스트")
    @CustomMockUser
    void update_posts_valid() throws Exception {
        Long postId = 1L;
        String password = "1234";
        String file = "Test";

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setPostTitle("새로운 제목");
        userUpdateRequest.setPostPassword(password);
        MockMultipartFile multipartFile = new MockMultipartFile("file", file, "image", "test data".getBytes());

        MockMultipartFile updatePostDto = new MockMultipartFile("userUpdateRequest", null, "application/json", objectMapper.writeValueAsString(userUpdateRequest).getBytes(StandardCharsets.UTF_8));
        doNothing().when(postService).update(postId,1l,userUpdateRequest,multipartFile);

        mockMvc.perform(multipart("/api/user/update/posts/"+postId)
                        .file(multipartFile)
                        .file(updatePostDto)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("실패"))
                .andExpect(jsonPath("$.data.size()").value("4"));
    }
    
    @Test
    @DisplayName("게시글 삭제 API 테스트")
    @CustomMockUser
    void delete_posts() throws Exception {
        Long postId = 1l;
        doNothing().when(postService).delete(anyLong(),anyLong());

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
        doThrow(new IllegalArgumentException("작성자만 삭제할수 있습니다."))
                .when(postService).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/api/user/posts/"+postId))
                .andDo(print())
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("작성자만 삭제할수 있습니다."));
    }

    @Test
    @DisplayName("게시글 비밀번호 유효성 검사 불일치")
    @CustomMockUser
    void validPost_invalidPassword_throwsBadRequestException() throws Exception {
        doThrow(new InvalidIdException("비밀번호가 일치하지 않습니다."))
                .when(postService).validPostPassword(anyLong(), any());

        ValidPostRequest validPostRequest = new ValidPostRequest();
        validPostRequest.setPassword("1234");
        validPostRequest.setPostId(1l);

        mockMvc.perform(post("/api/user/valid/posts")
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

        doNothing().when(postService).validPostPassword(anyLong(),eq(validPostRequest));
        mockMvc.perform(post("/api/user/valid/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPostRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비밀번호 인증 성공"));
    }

}