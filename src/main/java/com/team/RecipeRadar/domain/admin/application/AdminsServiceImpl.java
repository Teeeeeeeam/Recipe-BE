package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.dao.BlackList;
import com.team.RecipeRadar.domain.admin.domain.BlackListRepository;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dao.ingredient.IngredientRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.domain.RecipeBookmark;
import com.team.RecipeRadar.global.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminsServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final RecipeRepository recipeRepository;
    private final NoticeRepository noticeRepository;
    private final RecipeBookmarkRepository recipeBookmarkRepository;
    private final JWTRefreshTokenRepository jwtRefreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final CommentRepository commentRepository;
    private final ImgRepository imgRepository;
    private final S3UploadService s3UploadService;
    private final IngredientRepository ingredientRepository;

    @Override
    public long searchAllMembers() {
        return memberRepository.countAllBy();
    }

    @Override
    public long searchAllPosts() {
        return postRepository.countAllBy();
    }

    @Override
    public long searchAllRecipes() {
        return recipeRepository.countAllBy();
    }

    @Override
    public MemberInfoResponse memberInfos(Long lastMemberId,Pageable pageable) {
        Slice<MemberDto> memberInfo = memberRepository.getMemberInfo(lastMemberId,pageable);
        boolean hasNext = memberInfo.hasNext();
        MemberInfoResponse memberInfoResponse = new MemberInfoResponse(memberInfo.getContent(), hasNext);
        return memberInfoResponse;
    }


    /**
     * 여러명의 회원을 한번에 삭제 시킨다.
     * @param memberIds
     */
    @Override
    public List<String> adminDeleteUsers(List<Long> memberIds) {

        List<String> emailList = new ArrayList<>();

        for (Long memberId : memberIds) {
            Member member =memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("사용자를 찾을수 없습니다."));
            boolean existsByEmail = blackListRepository.existsByEmail(member.getEmail());
            if (!existsByEmail) {
                BlackList blackList = BlackList.toEntity(member.getEmail());
                emailList.add(member.getEmail());
                blackListRepository.save(blackList);
            }

            Long save_memberId = member.getId();
            commentRepository.deleteMember_comment(save_memberId);
            imgRepository.deleteMemberImg(save_memberId);
            recipeBookmarkRepository.deleteByMember_Id(save_memberId);
            jwtRefreshTokenRepository.DeleteByMemberId(save_memberId);
            memberRepository.deleteById(save_memberId);
        }

        return emailList;
    }

    @Override
    public MemberInfoResponse searchMember(String loginId, String nickname, String email, String username,Long lastMemberId,Pageable pageable) {
        Slice<MemberDto> memberDtoSlice = memberRepository.searchMember(loginId, nickname, email, username, lastMemberId,pageable);
        return new MemberInfoResponse(memberDtoSlice.getContent(),memberDtoSlice.hasNext());
    }

    @Override
    public PostsCommentResponse getPostsComments(Long postId, Long lastId, Pageable pageable) {
        Slice<CommentDto> postComment = commentRepository.getPostComment(postId, lastId, pageable);

        return new PostsCommentResponse(postComment.hasNext(),postComment.getContent());
    }

    /**
     * 어디민 사용자는 댓글을 단일,일괄 삭제
     * @param ids
     */
    @Override
    public void deleteComments(List<Long> ids) {
        for (Long id : ids) {
            Comment comment = commentRepository.findById(id).orElseThrow(() -> new NoSuchElementException("댓글을 찾을수 없습니다."));
            commentRepository.deleteById(comment.getId());
        }
    }

    @Override
    public void deleteRecipe(List<Long> ids) {
        for (Long id : ids){
            Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new BadRequestException("해당 레시피를 찾을수 없습니다."));
            List<UploadFile> byRecipeId = imgRepository.findAllByRecipeId(recipe.getId());
            for (UploadFile uploadFile : byRecipeId) {
                imgRepository.deleteById(uploadFile.getId());
                s3UploadService.deleteFile(uploadFile.getStoreFileName());
            }
            List<Post> allByRecipeId = postRepository.findAllByRecipeId(recipe.getId());
            for (Post post : allByRecipeId) {
                postRepository.deleteById(post.getId());
            }

            List<RecipeBookmark> allByRecipeId1 = recipeBookmarkRepository.findAllByRecipeId(recipe.getId());
            for (RecipeBookmark recipeBookmark : allByRecipeId1) {
                recipeBookmarkRepository.deleteById(recipeBookmark.getId());
            }
            ingredientRepository.deleteRecipeId(recipe.getId());
            recipeRepository.deleteById(recipe.getId());
        }
    }
}
