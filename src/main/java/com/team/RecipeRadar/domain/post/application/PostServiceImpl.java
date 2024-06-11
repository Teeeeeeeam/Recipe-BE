package com.team.RecipeRadar.domain.post.application;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.like.dao.PostLikeRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.user.*;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.domain.post.exception.PostException;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.Image.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
     * 게시글 저장을 저장하는 로직
     * @param userAddPostDto
     */
    @Override
    public void save(UserAddRequest userAddPostDto, MultipartFile file) {
        Long memberId = userAddPostDto.getMemberId();

        Optional<Member> op_member = memberRepository.findById(memberId);
        Optional<Recipe> op_recipe = recipeRepository.findById(userAddPostDto.getRecipe_id());

        if(op_member.isPresent()&& op_recipe.isPresent()) {
            Member member= op_member.get();
            Recipe recipe = op_recipe.get();
            String storedFileName = s3UploadService.uploadFile(file);       //s3에 이미지 저장

            Post post = Post.builder()
                    .postTitle(userAddPostDto.getPostTitle())
                    .postContent(userAddPostDto.getPostContent())
                    .postServing(userAddPostDto.getPostServing())
                    .postCookingTime(userAddPostDto.getPostCookingTime())
                    .postCookingLevel(userAddPostDto.getPostCookingLevel())
                    .postLikeCount(0) // 좋아요 초기값 설정
                    .member(member)
                    .postPassword(passwordEncoder.encode(userAddPostDto.getPostPassword()))
                    .recipe(recipe)
                    .created_at(LocalDateTime.now())
                    .build();

            UploadFile uploadFile = UploadFile.builder().originFileName(file.getOriginalFilename()).storeFileName(storedFileName).recipe(recipe).post(post).build();
            imgRepository.save(uploadFile);
            postRepository.save(post);
        } else {
            // 데이터베이스 저장 중에 문제가 발생한 경우
            throw new NoSuchElementException("요리글 저장에 실패했습니다.");
        }
    }

    /**
     * 게시글의모든 데이터를 무한 페이징 최신순으로 내림차순
     * @param pageable
     * @return
     */
    @Override
    public PostResponse postPage(Long postId,Pageable pageable) {
        Slice<PostDto> allPost = postRepository.getAllPost(postId,pageable);

        return new PostResponse(allPost.hasNext(),allPost.getContent());
    }

    @Override
    public Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostException("찾을 수 없습니다." + id));
    }

    /**
     * 게시글을 삭제하는 로직
     * @param loginId   로그인한 사용자의 loginId
     * @param postId    삭제할 게시글 id
     */
    @Override
    public void delete(String loginId, Long postId) {

        Member member = memberRepository.findByLoginId(loginId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("게시글을 찾을수 없습니다."));
        if(!post.getMember().getLoginId().equals(member.getLoginId())) throw new AccessDeniedException("작성자만 삭제할수 있습니다.");

        imgRepository.deletePostImg(post.getId(),post.getRecipe().getId());
        commentRepository.deletePostID(post.getId());
        postLikeRepository.deletePostID(postId);
        postRepository.deleteMemberId(member.getId(),postId);
    }

    /**
     * 게시글의 상제 정보를 보기위한 로직 해당 로직은 그저 전달체
     */
    @Override
    public PostDetailResponse postDetail(Long postId) {
        return postRepository.postDetails(postId);
    }

    /**
     * 게시글을 업데이트 하기 위한 로직
     */
    @Override
    public void update(Long postId,UserUpdateRequest userUpdateRequest,String loginId,MultipartFile file) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("해당 게시물을 찾을 수 없습니다."));
        if(!post.getMember().getLoginId().equals(loginId)) throw new AccessDeniedException("작성자만 삭제 가능합니다.");

        UploadFile uploadFile = imgRepository.getOriginalFileName(post.getId());
        if(file!=null) {
            if (!uploadFile.getOriginFileName().equals(file.getOriginalFilename())) {       // 원본파일명이 다를경우에만 s3에 기존 사진을 삭제 후 새롭게 저장
                s3UploadService.deleteFile(uploadFile.getStoreFileName());
                String storedFileName = s3UploadService.uploadFile(file);
                uploadFile.update(storedFileName, file.getOriginalFilename());
                imgRepository.save(uploadFile);
            }
        }

        post.update(userUpdateRequest.getPostTitle(), userUpdateRequest.getPostContent(), userUpdateRequest.getPostServing(),
                userUpdateRequest.getPostCookingTime(), userUpdateRequest.getPostCookingLevel(),passwordEncoder.encode(userUpdateRequest.getPostPassword()));

        postRepository.save(post);
    }

    @Override
    public UserInfoPostResponse userPostPage(String authenticationName,Long lastId, String loginId, Pageable pageable) {
        Member member = memberRepository.findByLoginId(loginId);

        if (member==null||!member.getUsername().equals(authenticationName)){
            throw new AccessDeniedException("접근할 수 없는 사용자입니다.");
        }

        Slice<UserInfoPostRequest> userInfoPostDto = postRepository.userInfoPost(member.getId(),lastId, pageable);

        return UserInfoPostResponse.builder()
                .nextPage(userInfoPostDto.hasNext())
                .content(userInfoPostDto.getContent()).build();
    }

    /**
     * 게시글 수정,삭제를하기전에 해당 게시글 등록시 비밀번호 사용해 해당 접근하려는 사용자가 작성한 사용자인지 검증하는 로직
     */
    @Override
    public boolean validPostPassword(String loginId, ValidPostRequest request) {
        Member byLoginId = memberRepository.findByLoginId(loginId);

        Post post = postRepository.findById(request.getPostId()).orElseThrow(() -> new NoSuchElementException("게시글을 찾을수 없습니다."));

        if(!post.getMember().getId().equals(byLoginId.getId()))
            throw new AccessDeniedException("작성한 사용자만 가능합니다.");

        if(!passwordEncoder.matches(request.getPassword(), post.getPostPassword()))
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");

        return true;
    }

    /**
     * dao 넘어온 PostDto의 페이징의 대한 데이터를 PostResponse의 담아서 변환
     */
    @Override
    public PostResponse searchPost(String loginId, String recipeTitle, String postTitle, Long lastPostId, Pageable pageable) {
        Slice<PostDto> postDtos = postRepository.searchPosts(loginId, recipeTitle, postTitle, lastPostId, pageable);
        return new PostResponse(postDtos.hasNext(),postDtos.getContent());
    }

    /**
     * 한개 이상의 게시글을 삭제할수 있다.
     * @param postIds
     */
    @Override
    public void deletePosts(List<Long> postIds) {

        for (Long postId : postIds) {
            Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("해당 게시물을 찾을수 없습니다."));
            imgRepository.deletePostImg(post.getId(),post.getRecipe().getId());
            commentRepository.deletePostID(post.getId());
            postLikeRepository.deletePostID(postId);
            postRepository.deleteById(post.getId());
        }
    }

}
