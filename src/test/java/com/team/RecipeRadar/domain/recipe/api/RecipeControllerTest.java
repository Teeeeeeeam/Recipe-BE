package com.team.RecipeRadar.domain.recipe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.application.RecipeBookmarkService;
import com.team.RecipeRadar.domain.recipe.application.RecipeServiceImpl;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.BookMarkRequest;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDetailsResponse;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.recipe.dto.RecipeResponse;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RecipeBookmarkService recipeBookmarkService;
    @MockBean RecipeServiceImpl recipeService;


    @MockBean
    MemberRepository memberRepository;
    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    CustomOauth2Handler customOauth2Handler;
    @MockBean
    CustomOauth2Service customOauth2Service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @CustomMockUser
    @DisplayName("즐겨찾기를 성공하는 테스트")
    void bookmark_success_test() throws Exception {
        Member member = Member.builder().id(1l).loginId("Test").username("loginId").username("username").build();
        Recipe recipe = Recipe.builder().id(2l).likeCount(1).title("title").build();

        given(recipeBookmarkService.saveBookmark(member.getId(),recipe.getId())).willReturn(false);

        BookMarkRequest bookMarkRequest = new BookMarkRequest("1", recipe.getId());

        mockMvc.perform(post("/api/user/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMarkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("성공"))
                .andExpect(jsonPath("$.data.['즐겨 찾기 상태']").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("즐겨찾기를 헤제하는 테스트")
    @CustomMockUser
    void unBookmark_success_test() throws Exception {
        Member member = Member.builder().id(1l).loginId("Test").username("loginId").username("username").build();
        Recipe recipe = Recipe.builder().id(2l).likeCount(1).title("title").build();

        given(recipeBookmarkService.saveBookmark(member.getId(),recipe.getId())).willReturn(true);

        BookMarkRequest bookMarkRequest = new BookMarkRequest("1", recipe.getId());

        mockMvc.perform(post("/api/user/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMarkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("성공"))
                .andExpect(jsonPath("$.data.['즐겨 찾기 상태']").value(true))
                .andDo(print());
    }

    @Test
    @CustomMockUser
    @DisplayName("사용자가 즐겨찾기를 진행하려했으나 db에 정보가 없을때 예외")
    void BadRequest_Bookmark_test()throws Exception{
        Member member = Member.builder().id(1l).loginId("Test").username("loginId").username("username").build();
        Recipe recipe = Recipe.builder().id(3l).likeCount(1).title("title").build();

        given(recipeBookmarkService.saveBookmark(member.getId(),recipe.getId())).willThrow(new NoSuchElementException("사용자 및 레시피를 찾을수 없습니다."));

        BookMarkRequest bookMarkRequest = new BookMarkRequest("1", recipe.getId());

        mockMvc.perform(post("/api/user/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMarkRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("사용자 및 레시피를 찾을수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("재료 검색 레시피 조회 테스트")
    void Search_Recipe() throws Exception {

        List<String> ingredients = Arrays.asList("밥");
        Pageable pageRequest = PageRequest.of(0, 2);

        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1L, "url1", "레시피1", "level1", "1인분", "10분", 0));
        recipeDtoList.add(new RecipeDto(2L, "url2", "레시피2", "level2", "2인분", "1시간", 0));

        boolean paged = pageRequest.next().isPaged();

        RecipeResponse recipeResponse = new RecipeResponse(recipeDtoList, paged);

        given(recipeService.searchRecipesByIngredients(eq(ingredients), eq(1l),any(Pageable.class)))
                .willReturn(recipeResponse);

        mockMvc.perform(get("/api/recipe?ingredients=밥&lastId=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipeDtoList.[0].id").value(1))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].imageUrl").value("url1"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].title").value("레시피1"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].cookingLevel").value("level1"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].people").value("1인분"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].cookingTime").value("10분"))
                .andExpect(jsonPath("$.data.recipeDtoList.size()").value(2));
    }

    @Test
    @DisplayName("레시피 상세 페이지 조회 테스트")
    void getDetails_Recipe() throws Exception {

        Long id = 1l;
        RecipeDto recipeDto = RecipeDto.builder().id(id).cookingLevel("11").title("title").build();
        List<String> ing= Arrays.asList("밥","고기","김치");
        List<String> cookSetp = Arrays.asList("조리1","조리2","조리3");
        RecipeDetailsResponse recipeDetailsResponse = RecipeDetailsResponse.of(recipeDto, ing, cookSetp);
        given(recipeService.getRecipeDetails(eq(1l))).willReturn(recipeDetailsResponse);

        mockMvc.perform(get("/api/recipe/"+id)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipe.id").value("1"))
                .andExpect(jsonPath("$.data.recipe.title").value("title"))
                .andExpect(jsonPath("$.data.ingredients.size()").value(3))
                .andExpect(jsonPath("$.data.cookStep.size()").value(3));
    }
}