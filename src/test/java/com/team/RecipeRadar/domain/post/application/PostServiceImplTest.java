package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.like.dao.PostLikeRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.dto.user.PostResponse;
import com.team.RecipeRadar.domain.post.dto.user.UserAddRequest;
import com.team.RecipeRadar.domain.post.dto.user.UserUpdateRequest;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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

    @InjectMocks PostServiceImpl postService;

    @Test
    @DisplayName("사용자 페이지-작성한 게시글 조회 성공시 테스트")
    void userPostPage_success(){
        Long memberId = 1l;
        String auName="username";
        String loginId = "loginId";

        Member member = Member.builder().id(memberId).username("username").loginId(loginId).build();
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        Pageable pageable = PageRequest.of(0, 2);

        List<UserInfoPostRequest> requests = new ArrayList<>();
        requests.add(new UserInfoPostRequest(3l,"타이틀1"));
        requests.add(new UserInfoPostRequest(4l,"타이틀2"));

        SliceImpl<UserInfoPostRequest> userInfoPostRequests = new SliceImpl<>(requests, pageable, false);

        when(postRepository.userInfoPost(memberId,pageable)).thenReturn(userInfoPostRequests);

        UserInfoPostResponse userInfoPostResponse = postService.userPostPage(auName, loginId, pageable);
        assertThat(userInfoPostResponse.isNextPage()).isFalse();
        assertThat(userInfoPostResponse.getContent()).isNotEmpty();
        assertThat(userInfoPostResponse.getContent().size()).isEqualTo(2);
        assertThat(userInfoPostResponse.getContent().get(0).getPostTitle()).isEqualTo("타이틀1");
    }

    @Test
    @DisplayName("사용자 페이지-작성한 게시글 조회 실패시 테스트")
    void userPostPage_fail(){
        Long memberId = 1l;
        String auName="failUsername";
        String loginId = "loginId";

        Member member = Member.builder().id(memberId).username("username").loginId(loginId).build();
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        Pageable pageable = PageRequest.of(0, 2);

        assertThatThrownBy(() -> postService.userPostPage(auName, loginId, pageable)).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("게시글 등록 테스트 반환 타입이 void라 1회 호출됬는지 확이")
    void newPost_save(){
        Long memberId = 1l;
        Long recipeId = 2l;
        String password = "1234";

        Member member = Member.builder().id(memberId).loginId("testId").build();
        Recipe recipe = Recipe.builder().id(recipeId).title("레시피 제목").build();

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(recipeRepository.findById(eq(recipeId))).thenReturn(Optional.of(recipe));

        when(passwordEncoder.encode(anyString())).thenReturn(password);

        Post post = Post.builder().id(3l).member(member).recipe(recipe).build();
        when(postRepository.save(any())).thenReturn(post);

        UserAddRequest userAddRequest = new UserAddRequest();
        userAddRequest.setPostContent("컨텐트");
        userAddRequest.setRecipe_id(recipeId);
        userAddRequest.setMemberId(memberId);
        userAddRequest.setPostPassword(password);

        postService.save(userAddRequest);

        verify(postRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("게시글 조회 무한 페이징 테스트")
    void paging(){
        Pageable pageRequest = PageRequest.of(0, 2);
        List<PostDto> postDtos = List.of(PostDto.builder().postContent("컨텐트").id(1l).build(), PostDto.builder().postContent("컨텐트2").id(2l).build());
        SliceImpl<PostDto> dtoSlice = new SliceImpl<>(postDtos , pageRequest, false);       // 다음 페이지는 없음
        when(postRepository.getAllPost(eq(pageRequest))).thenReturn(dtoSlice);

        PostResponse postResponse = postService.postPage(pageRequest);
        assertThat(postResponse.getPosts()).hasSize(2);
        assertThat(postResponse.getPosts().get(0).getPostContent()).isEqualTo("컨텐트");
        assertThat(postResponse.isNextPage()).isFalse();
    }

    @Test
    @DisplayName("게시글 업데이트 테스트")
    void update_Posts(){
        Long postId = 1l;
        String password = "1234";
        String loginId = "testId";

        Member member = Member.builder().loginId(loginId).nickName("ssss").build();
        Post post = Post.builder().id(postId).postContent("내용").postTitle("제목").member(member).postPassword(password).build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        when(passwordEncoder.encode(anyString())).thenReturn(password);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setPostId(postId);
        userUpdateRequest.setPostPassword(password);
        userUpdateRequest.setPostContent("변경된 내용");
        postService.update(userUpdateRequest,loginId);

        assertThat(post.getPostContent()).isEqualTo("변경된 내용");
    }

    @Test
    @DisplayName("게시글 업데이트시 게시글이 존재하지 않을시")
    void update_none_posts(){
        Long postId = 1l;
        String loginId = "testId";

        when(postRepository.findById(eq(postId))).thenThrow(new NoSuchElementException("게시글을 찾을 수 없습니다."));

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setPostId(postId);

        assertThatThrownBy(() -> postService.update(userUpdateRequest, loginId)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("게시글 업데이트시 작성자가 아닐 경우 예외 발생 테스트")
    void update_not_author() {
        Long postId = 1L;
        String loginId = "testId";
        String anotherLoginId = "anotherId";
        String password = "1234";

        Member member = Member.builder().loginId(anotherLoginId).nickName("ssss").build();
        Post post = Post.builder().id(postId).postContent("내용").postTitle("제목").member(member).postPassword(password).build();

        when(postRepository.findById(eq(postId))).thenReturn(Optional.of(post));

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setPostId(postId);
        userUpdateRequest.setPostTitle("새로운 제목");
        userUpdateRequest.setPostContent("새로운 내용");
        userUpdateRequest.setPostServing("4인분");
        userUpdateRequest.setPostCookingTime("45분");
        userUpdateRequest.setPostCookingLevel("중간");
        userUpdateRequest.setPostImageUrl("새로운 이미지 URL");
        userUpdateRequest.setPostPassword(password);

        assertThatThrownBy(() -> postService.update(userUpdateRequest, loginId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("작성자만 삭제 가능합니다.");
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void delete_posts(){
        String loginId= "testId";
        Long postId = 1l;
        Member member = Member.builder().id(1l).loginId(loginId).build();
        Post post = Post.builder().id(postId).postTitle("제목").member(member).build();
        when(memberRepository.findByLoginId(eq(loginId))).thenReturn(member);
        when(postRepository.findById(eq(postId))).thenReturn(Optional.of(post));

        postService.delete(loginId,postId);

        verify(commentRepository,times(1)).deletePostID(anyLong());
        verify(postLikeRepository,times(1)).deletePostID(anyLong());
        verify(postRepository,times(1)).deleteMemberId(anyLong(),anyLong());
    }

    @Test
    @DisplayName("게시글 삭제시 게시글 없을시")
    void delete_noSuch_posts(){
        String loginId= "testId";
        Long postId = 1l;
        when(memberRepository.findByLoginId(eq(loginId))).thenThrow(new NoSuchElementException("게시글을 찾을수 없습니다."));

        assertThatThrownBy(() -> postService.delete(loginId,postId)).isInstanceOf(NoSuchElementException.class);
    }

}