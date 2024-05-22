package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    long searchAllMembers();

    long searchAllPosts();

    long searchAllRecipes();

    MemberInfoResponse memberInfos(Pageable pageable);

    void adminDeleteUser(Long memberId);

    void adminDeleteUsers(List<Long> memberIds);

}
