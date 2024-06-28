package com.team.RecipeRadar.domain.post.application.user;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.like.dao.like.PostLikeRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.request.UserAddRequest;
import com.team.RecipeRadar.domain.post.dto.request.UserUpdateRequest;
import com.team.RecipeRadar.domain.post.dto.request.ValidPostRequest;
import com.team.RecipeRadar.domain.post.dto.response.*;
import com.team.RecipeRadar.domain.post.dto.request.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.response.UserInfoPostResponse;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final RecipeRepository recipeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImgRepository imgRepository;
    private final S3UploadService s3UploadService;


    /**
     * 게시글 저장을 저장하는 메서드
     */
    @Override
    public void save(UserAddRequest userAddRequest, Long memberId , MultipartFile file) {
        Member member = getMember(memberId);
        Recipe recipe = recipeRepository.findById(userAddRequest.getRecipeId()).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_RECIPE));

        Post post = Post.createPost(userAddRequest.getPostTitle(), userAddRequest.getPostContent(), userAddRequest.getPostServing(), userAddRequest.getPostCookingTime(),
                userAddRequest.getPostCookingLevel(), member, recipe, passwordEncoder.encode(userAddRequest.getPostPassword()));

        s3UploadService.uploadFile(file,List.of(post,recipe));

        postRepository.save(post);
    }

    /**
     * 게시글의모든 데이터를 무한 페이징 최신순으로 내림차순
     */
    @Override
    public PostResponse postPage(Long postId,Pageable pageable) {
        Slice<PostDto> allPost = postRepository.getAllPost(postId,pageable);
        return new PostResponse(allPost.hasNext(),allPost.getContent());
    }

    /**
     * 게시글을 삭제하는 메서드
     */
    @Override
    public void delete(Long memberId, Long postId) {
        Member member = getMember(memberId);
        Post post = getPost(postId);
        validatePostOwner(member, post);
        delete(member, post);
    }

    /**
     * 게시글의 상제 정보를 보기위한 로직 해당 로직은 그저 전달체
     */
    @Override
    public PostDetailResponse postDetail(Long postId) {
        PostDto postDto = postRepository.postDetails(postId);
        return new PostDetailResponse(postDto);
    }

    /**
     * 게시글을 업데이트 하기 위한 메서드
     */
    @Override
    public void update(Long postId, Long memberId, UserUpdateRequest userUpdateRequest, MultipartFile file) {

        Member member = getMember(memberId);
        Post post = getPost(postId);
        validatePostOwner(member,post);

        s3UploadService.updateFile(file,List.of(post));

        post.update(userUpdateRequest.getPostTitle(), userUpdateRequest.getPostContent(), userUpdateRequest.getPostServing(),
                userUpdateRequest.getPostCookingTime(), userUpdateRequest.getPostCookingLevel(),passwordEncoder.encode(userUpdateRequest.getPostPassword()));
    }

    /**
     * 마이페이지에서 사용자가 작성한 게시글을 조회하는 메서드
     *
     */
    @Override
    public UserInfoPostResponse userPostPage(Long memberId,Long lastId, Pageable pageable) {
        Member member = getMember(memberId);

        Slice<UserInfoPostRequest> userInfoPostDto = postRepository.userInfoPost(member.getId(),lastId, pageable);

        return new UserInfoPostResponse(userInfoPostDto.hasNext(),userInfoPostDto.getContent());
    }

    /**
     * 게시글 수정,삭제를하기전에 해당 게시글 등록시 비밀번호 사용해 해당 접근하려는 사용자가 작성한 사용자인지 검증하는 로직
     */
    @Override
    public void validPostPassword(Long memberId, ValidPostRequest validPostRequest) {
        Member member = getMember(memberId);
        Post post = getPost(validPostRequest.getPostId());
        validatePassword(validPostRequest, post);
        validatePostOwner(member,post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostLikeTopResponse getTop4RecipesByLikes(Long recipeId) {
        List<PostDto> topRecipesByLikes = postRepository.getTopRecipesByLikes(recipeId);
        return new PostLikeTopResponse(topRecipesByLikes);
    }

    @Override
    @Transactional(readOnly = true)
    public PostLikeTopResponse getTopMainsByLikes() {
        return new PostLikeTopResponse(postRepository.getTopMainByLikes());
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse postByRecipeId(Long recipeId,Integer lastCount,Long lastId, Pageable pageable) {
        Slice<PostDto> postDtoSlice = postRepository.getPostsByRecipeId(recipeId,lastCount,lastId, pageable);
        return new PostResponse(postDtoSlice.hasNext(),postDtoSlice.getContent());
    }


    /* 게시글의 비밀 번호를 검증하는 메서드 */
    private void validatePassword(ValidPostRequest validPostRequest, Post post) {
        if(!passwordEncoder.matches(validPostRequest.getPassword(), post.getPostPassword()))
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
    }

    /* 사용자 정보 조회 메서드*/
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
    }
    
    /*게시글 조회 메서드*/
    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_POST));
    }

    /* 작성자 및 관리자인지 검증 메서드*/
    private static void validatePostOwner(Member member, Post post) {
        if(!post.getMember().getLoginId().equals(member.getLoginId()) && !member.getRoles().equals("ROLE_ADMIN"))
            throw new IllegalArgumentException("작성자만 이용 가능합니다.");
    }

    /* 삭제 메서드 */
    private void delete(Member member, Post post) {
        String storeFileName = imgRepository.findByPostId(post.getId()).getStoreFileName();
        s3UploadService.deleteFile(storeFileName);
        imgRepository.deletePostImg(post.getId(), post.getRecipe().getId());
        commentRepository.deleteByPostId(post.getId());
        postLikeRepository.deletePostID(post.getId());
        postRepository.deleteMemberId(member.getId(), post.getId());
    }

}
