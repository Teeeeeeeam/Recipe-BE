package com.team.RecipeRadar.domain.bookmark.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.bookmark.application.RecipeBookmarkService;
import com.team.RecipeRadar.domain.bookmark.dto.reqeust.BookMarkRequest;
import com.team.RecipeRadar.domain.bookmark.dto.response.UserInfoBookmarkResponse;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.api.user.RecipeController;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.application.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.application.CustomOauth2Service;
import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.mock.CustomMockUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest {

    @MockBean RecipeBookmarkService recipeBookmarkService;
    @MockBean CookieUtils cookieUtils;
    @Autowired MockMvc mockMvc;

    @MockBean MemberRepository memberRepository;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @CustomMockUser
    @DisplayName("즐겨찾기를 성공하는 테스트")
    void bookmark_success_test() throws Exception {
        Member member = Member.builder().id(1l).loginId("Test").username("loginId").username("username").build();
        Recipe recipe = Recipe.builder().id(2l).likeCount(1).title("title").build();

        given(recipeBookmarkService.saveBookmark(member.getId(),recipe.getId())).willReturn(false);

        BookMarkRequest bookMarkRequest = new BookMarkRequest(recipe.getId());

        mockMvc.perform(post("/api/user/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMarkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("북마크 성공"))
                .andDo(print());
    }

    @Test
    @DisplayName("즐겨찾기를 헤제하는 테스트")
    @CustomMockUser
    void unBookmark_success_test() throws Exception {
        Member member = Member.builder().id(1l).loginId("Test").username("loginId").username("username").build();
        Recipe recipe = Recipe.builder().id(2l).likeCount(1).title("title").build();

        given(recipeBookmarkService.saveBookmark(member.getId(),recipe.getId())).willReturn(true);

        BookMarkRequest bookMarkRequest = new BookMarkRequest(recipe.getId());

        mockMvc.perform(post("/api/user/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMarkRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("북마크 해제"))
                .andDo(print());
    }

    @Test
    @CustomMockUser
    @DisplayName("사용자가 즐겨찾기를 진행하려했으나 db에 정보가 없을때 예외")
    void BadRequest_Bookmark_test()throws Exception{
        Member member = Member.builder().id(1l).loginId("Test").username("loginId").username("username").build();
        Recipe recipe = Recipe.builder().id(3l).likeCount(1).title("title").build();

        given(recipeBookmarkService.saveBookmark(member.getId(),recipe.getId())).willThrow(new NoSuchElementException("사용자 및 레시피를 찾을수 없습니다."));

        BookMarkRequest bookMarkRequest = new BookMarkRequest(recipe.getId());

        mockMvc.perform(post("/api/user/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMarkRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("사용자 및 레시피를 찾을수 없습니다."))
                .andDo(print());
    }

    @Test
    @CustomMockUser
    @DisplayName("사용자가 즐겨찾기한 레시피 제목 페이징")
    void bookmark_page() throws Exception {
        Long memberId =  1l;
        List<RecipeDto> list = List.of(RecipeDto.builder().id(1l).title("레시피1").build(),RecipeDto.builder().id(2l).title("레시피2").build(),RecipeDto.builder().id(3l).title("레시피3").build());

        UserInfoBookmarkResponse userInfoBookmarkResponse = new UserInfoBookmarkResponse(false, list);
        given(recipeBookmarkService.userInfoBookmark(eq(memberId),isNull(),any(Pageable.class))).willReturn(userInfoBookmarkResponse);

        Cookie cookie = new Cookie("login-id", "cookie");
        mockMvc.perform(get("/api/user/info/bookmark")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextPage").value(false))
                .andExpect(jsonPath("$.data.bookmarkList.size()").value(3));
    }

    @Test
    @CustomMockUser
    @DisplayName("사용자가 즐겨찾기한 레시피 제목 페이징(쿠키가 없을때 접근)")
    void bookmark_page_NONECOOKIE() throws Exception {

        doThrow(new UnauthorizedException("잘못된 접근 이거나 일반 사용자만 가능합니다.")).when(cookieUtils).validCookie(isNull(),anyString());

        mockMvc.perform(get("/api/user/info/bookmark"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("잘못된 접근 이거나 일반 사용자만 가능합니다."));
    }

    @Test
    @CustomMockUser(id = 2l)
    @DisplayName("사용자가 즐겨찾기한 레시피 제목 페이징(사용자가아닌 다른 사용자가 접근시)")
    void aasdasd() throws Exception {
        Long memberId =  1l;
        List<RecipeDto> list = List.of(RecipeDto.builder().id(1l).title("레시피1").build(),RecipeDto.builder().id(2l).title("레시피2").build(),RecipeDto.builder().id(3l).title("레시피3").build());

        UserInfoBookmarkResponse userInfoBookmarkResponse = new UserInfoBookmarkResponse(false, list);
        given(recipeBookmarkService.userInfoBookmark(eq(memberId),isNull(),any(Pageable.class))).willReturn(userInfoBookmarkResponse);
        Cookie cookie = new Cookie("login-id", "cookie");

        mockMvc.perform(get("/api/user/info/bookmark")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @CustomMockUser
    @DisplayName("로그인한 사용자 즐겨찾기 상태")
    void loginIsBookmark() throws Exception {
        Long recipeId = 1L;
        Long memberId = 1L;

        given(recipeBookmarkService.checkBookmark(eq(memberId), eq(recipeId))).willReturn(true);

        mockMvc.perform(get("/api/user/recipe/{recipeId}/bookmarks/check", recipeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @CustomMockUser(id = 2l)
    @DisplayName("로그인한 사용자(즐겨찾기 되어있지않은 사용자) 즐겨찾기 상태")
    void diff_loginIsBookmark() throws Exception {
        Long recipeId = 1L;
        Long memberId = 1L;

        given(recipeBookmarkService.checkBookmark(eq(memberId), eq(recipeId))).willReturn(true);

        mockMvc.perform(get("/api/user/recipe/{recipeId}/bookmarks/check", recipeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    @DisplayName("비로그인 사용자 즐겨찾기 상태")
    void UnLoginIsBookmark() throws Exception {
        Long recipeId = 1L;

        given(recipeBookmarkService.checkBookmark(isNull() ,eq(recipeId))).willReturn(false);

        mockMvc.perform(get("/api/user/recipe/{recipeId}/bookmarks/check", recipeId))
                .andDo(print())
                .andExpect(status().is(302));
    }
}