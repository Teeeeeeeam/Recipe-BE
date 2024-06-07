package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.dto.BlackListResponse;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    long searchAllMembers();

    long searchAllPosts();

    long searchAllRecipes();

    MemberInfoResponse memberInfos(Long lastMemberId,Pageable pageable);

    List<String> adminDeleteUsers(List<Long> memberIds);

    MemberInfoResponse searchMember(String loginId, String nickname, String email, String username,Long lastMemberId,Pageable pageable);

    PostsCommentResponse getPostsComments(Long postId,Long lastId,Pageable pageable);

    void deleteComments(List<Long> ids);

    void deleteRecipe(List<Long> ids);
    BlackListResponse getBlackList(Long lastId,Pageable pageable);
    boolean temporarilyUnblockUser(Long blackId);

}
