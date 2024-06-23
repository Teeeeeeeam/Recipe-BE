package com.team.RecipeRadar.domain.post.application.user;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.like.dao.like.PostLikeRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.request.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.response.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.dto.response.PostResponse;
import com.team.RecipeRadar.domain.post.dto.request.UserAddRequest;
import com.team.RecipeRadar.domain.post.dto.request.UserUpdateRequest;
import com.team.RecipeRadar.domain.post.dto.request.ValidPostRequest;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock PostRepository postRepository;
    @Mock MemberRepository memberRepository;
    @Mock RecipeRepository recipeRepository;
    @Mock CommentRepository commentRepository;
    @Mock PostLikeRepository postLikeRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock S3UploadService s3UploadService;
    @Mock ImgRepository imgRepository;

    @InjectMocks
    PostServiceImpl postService;

    @Test
    @DisplayName("사용자 페이지-작성한 게시글 조회 성공시 테스트")
    void userPostPage_success(){
        Long memberId = 1l;
        String loginId = "loginId";

        Member member = Member.builder().id(memberId).username("username").loginId(loginId).build();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        Pageable pageable = PageRequest.of(0, 2);

        List<UserInfoPostRequest> requests = new ArrayList<>();
        requests.add(new UserInfoPostRequest(3l,"타이틀1"));
        requests.add(new UserInfoPostRequest(4l,"타이틀2"));

        SliceImpl<UserInfoPostRequest> userInfoPostRequests = new SliceImpl<>(requests, pageable, false);

        when(postRepository.userInfoPost(memberId,null,pageable)).thenReturn(userInfoPostRequests);

        UserInfoPostResponse userInfoPostResponse = postService.userPostPage(memberId, null, pageable);
        assertThat(userInfoPostResponse.isNextPage()).isFalse();
        assertThat(userInfoPostResponse.getContent()).isNotEmpty();
        assertThat(userInfoPostResponse.getContent().size()).isEqualTo(2);
        assertThat(userInfoPostResponse.getContent().get(0).getPostTitle()).isEqualTo("타이틀1");
    }

    @Test
    @DisplayName("게시글 등록 테스트 반환 타입이 void라 1회 호출됐는지 확인")
    void newPost_save() {
        Long memberId = 1L;
        Long recipeId = 2L;
        String image = "test";
        String password = "1234";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "Test.jpg", "image", "test data".getBytes());

        Member member = Member.builder().id(memberId).loginId("testId").build();
        Recipe recipe = Recipe.builder().id(recipeId).title("레시피 제목").build();

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(recipeRepository.findById(eq(recipeId))).thenReturn(Optional.of(recipe));
        when(s3UploadService.uploadFile(eq(multipartFile), anyList())).thenReturn(image);
        when(passwordEncoder.encode(anyString())).thenReturn(password);

        Post post = Post.builder().id(3L).member(member).recipe(recipe).build();
        when(postRepository.save(any())).thenReturn(post);

        UserAddRequest userAddRequest = new UserAddRequest();
        userAddRequest.setPostContent("컨텐츠");
        userAddRequest.setPostPassword(password);
        userAddRequest.setRecipeId(recipeId);  // recipeId 설정 추가

        postService.save(userAddRequest, memberId, multipartFile);

        verify(postRepository, times(1)).save(any());
    }


    @Test
    @DisplayName("게시글 조회 무한 페이징 테스트")
    void paging(){
        Pageable pageRequest = PageRequest.of(0, 2);
        List<PostDto> postDtos = List.of(PostDto.builder().postContent("컨텐트").id(1l).build(), PostDto.builder().postContent("컨텐트2").id(2l).build());
        SliceImpl<PostDto> dtoSlice = new SliceImpl<>(postDtos , pageRequest, false);       // 다음 페이지는 없음
        when(postRepository.getAllPost(anyLong(),eq(pageRequest))).thenReturn(dtoSlice);

        PostResponse postResponse = postService.postPage(1l,pageRequest);
        assertThat(postResponse.getPosts()).hasSize(2);
        assertThat(postResponse.getPosts().get(0).getPostContent()).isEqualTo("컨텐트");
        assertThat(postResponse.isNextPage()).isFalse();
    }

    @Test
    @DisplayName("게시글 업데이트 테스트")
    void update_Posts() {
        Long postId = 1L;
        String password = "1234";
        String originFile = "origin.jpg";
        String loginId = "testId";
        MockMultipartFile multipartFile = new MockMultipartFile("file", originFile, "image", "test data".getBytes());

        Member member = Member.builder().id(1L).loginId(loginId).nickName("ssss").build();
        Post post = Post.builder().id(postId).postContent("내용").postTitle("제목").member(member).postPassword(password).build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setPostPassword(password);
        userUpdateRequest.setPostContent("변경된 내용");

        postService.update(postId, 1L, userUpdateRequest, multipartFile);

        assertThat(post.getPostContent()).isEqualTo("변경된 내용");
    }

    @Test
    @DisplayName("게시글 업데이트시 게시글이 존재하지 않을시")
    void update_none_posts(){
        Long postId = 1l;
        MockMultipartFile multipartFile = new MockMultipartFile("file", "Test.jpg", "image", "test data".getBytes());
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        assertThatThrownBy(() -> postService.update(postId,1l,userUpdateRequest,multipartFile)).isInstanceOf(NoSuchDataException.class);
    }

    @Test
    @DisplayName("게시글 업데이트시 작성자가 아닐 경우 예외 발생 테스트")
    void update_not_author() {
        Long postId = 1L;
        Long memberId = 1L;
        Long anotherMemberId = 2L;
        String anotherLoginId = "anotherId";
        String password = "1234";

        Member member = Member.builder().id(memberId).loginId(anotherLoginId).nickName("ssss").roles("ROLE_USER").build();
        Post post = Post.builder().id(postId).postContent("내용").postTitle("제목").member(member).postPassword(password).build();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "Test.jpg", "image", "test data".getBytes());

        when(memberRepository.findById(eq(anotherMemberId))).thenReturn(Optional.of(Member.builder().id(anotherMemberId).roles("ROLE_USER").build()));
        when(postRepository.findById(eq(postId))).thenReturn(Optional.of(post));

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setPostTitle("새로운 제목");
        userUpdateRequest.setPostContent("새로운 내용");
        userUpdateRequest.setPostServing("4인분");
        userUpdateRequest.setPostCookingTime("45분");
        userUpdateRequest.setPostCookingLevel("중간");
        userUpdateRequest.setPostPassword(password);

        assertThatThrownBy(() -> postService.update(postId, anotherMemberId, userUpdateRequest, multipartFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("작성자만 이용 가능합니다.");
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void delete_posts(){
        Long memberId = 2L;
        Long postId = 1L;
        Member member = Member.builder().id(memberId).loginId("loginId").build();
        Recipe recipe = Recipe.builder().id(1L).title("ttt").build();
        Post post = Post.builder().id(postId).postTitle("제목").member(member).recipe(recipe).build();
        UploadFile uploadFile = UploadFile.builder().post(post).storeFileName("저장된 파일명").build();

        when(imgRepository.findByPostId(anyLong())).thenReturn(uploadFile);
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        postService.delete(memberId, postId);

        verify(commentRepository, times(1)).deletePostID(anyLong());
        verify(postLikeRepository, times(1)).deletePostID(anyLong());
        verify(postRepository, times(1)).deleteMemberId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 삭제시 게시글 없을시")
    void delete_noSuch_posts(){
        Long memberId = 2l;
        Long postId = 1l;
        when(memberRepository.findById(eq(memberId))).thenThrow(new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));

        assertThatThrownBy(() -> postService.delete(memberId,postId)).isInstanceOf(NoSuchDataException.class);
    }

    @Test
    @DisplayName("게시글 수정/삭제 진행시 비밀번호 검증 테스트")
    void valid_passwordTest(){
        String loginId = "testId";
        Long memberId = 2l;
        Long postId = 1l;
        String password="1234";

        Member member = Member.builder().id(memberId).loginId(loginId).nickName("닉네임").build();
        Post post = Post.builder().id(postId).postTitle("제목").member(member).postPassword(password).build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.findById(eq(postId))).thenReturn(Optional.of(post));


        ValidPostRequest validPostRequest = new ValidPostRequest();
        validPostRequest.setPassword(password);
        validPostRequest.setPostId(postId);
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);

        postService.validPostPassword(memberId, validPostRequest);

    }

    @Test
    @DisplayName("게시글 수정/삭제 진행시 비밀번호  일치하지 않을때 검증 테스트")
    void valid_passwordTest_notSame(){
        String loginId = "testId";
        Long postId = 1l;
        String password="1234";

        Member member = Member.builder().id(1l).loginId(loginId).nickName("닉네임").build();
        Post post = Post.builder().id(postId).postTitle("제목").member(member).postPassword(password).build();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.findById(eq(postId))).thenReturn(Optional.of(post));


        ValidPostRequest validPostRequest = new ValidPostRequest();
        validPostRequest.setPassword(password);
        validPostRequest.setPostId(postId);
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(false);

        assertThatThrownBy(() ->  postService.validPostPassword(1l, validPostRequest)).isInstanceOf(IllegalStateException.class).hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("작성자가 아닐 시 게시글 비밀번호 유효성 검사 실패 테스트")
    void validPostPassword_notAuthor_throwsUnauthorizedException() {
        String loginId = "testUser";
        Long postId = 1L;
        String providedPassword = "password";

        Member member = Member.builder().id(1L).loginId(loginId).build();
        Member differentMember = Member.builder().id(2L).loginId("anotherUser").build();
        Post post = Post.builder().id(postId).member(differentMember).postPassword(passwordEncoder.encode("password")).build();

        ValidPostRequest request = new ValidPostRequest();
        request.setPostId(postId);
        request.setPassword(providedPassword);


        when(memberRepository.findById(2L)).thenReturn(Optional.of(member)); // 다른 사용자가 호출한 것으로 가정
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.validPostPassword(2L, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

}