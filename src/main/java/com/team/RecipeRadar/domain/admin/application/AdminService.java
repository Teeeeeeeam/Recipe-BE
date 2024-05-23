package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    long searchAllMembers();

    long searchAllPosts();

    long searchAllRecipes();

    MemberInfoResponse memberInfos(Long lastMemberId,Pageable pageable);

    void adminDeleteUsers(List<Long> memberIds);

    MemberInfoResponse searchMember(String loginId, String nickname, String email, String username,Long lastMemberId,Pageable pageable);

    PostsCommentResponse getPostsComments(Long postId,Long lastId,Pageable pageable);

    void deleteComments(List<Long> ids);

}
