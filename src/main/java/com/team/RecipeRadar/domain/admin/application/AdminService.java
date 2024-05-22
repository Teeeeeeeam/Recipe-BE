package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    long searchAllMembers();

    long searchAllPosts();

    long searchAllRecipes();

    MemberInfoResponse memberInfos(Pageable pageable);

    void adminDeleteUser(Long memberId);

}
