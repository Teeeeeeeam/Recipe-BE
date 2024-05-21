package com.team.RecipeRadar.domain.admin.api;

import com.team.RecipeRadar.domain.admin.application.AdminService;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/members/count")
    public ResponseEntity<?> getAllMembersCount(){
        long searchAllMembers = adminService.searchAllMembers();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }

    @GetMapping("/posts/count")
    public ResponseEntity<?> getAllPostsCount(){
        long searchAllMembers = adminService.searchAllPosts();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }
    @GetMapping("/recipes/count")
    public ResponseEntity<?> getAllRecipesCount(){
        long searchAllMembers = adminService.searchAllRecipes();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }
}
