package com.team.RecipeRadar.domain.bookmark.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.bookmark.application.RecipeBookmarkService;
import com.team.RecipeRadar.domain.bookmark.dto.reqeust.BookMarkRequest;
import com.team.RecipeRadar.domain.bookmark.dto.response.UserInfoBookmarkResponse;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityTestConfig.class)
@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest {

    @MockBean RecipeBookmarkService recipeBookmarkService;
    @MockBean CookieUtils cookieUtils;
    @Autowired MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    private Member member;
    private Recipe recipe;
    private Long memberId = 1L;
    private Long recipeId = 1L;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1L).loginId("Test").username("loginId").username("username").build();
        recipe = Recipe.builder().id(2L).likeCount(1).title("title").build();
    }

    @Test
    @CustomMockUser
    @DisplayName("즐겨찾기 추가 성공 테스트")
    void bookmark_success_test() throws Exception {
        given(recipeBookmarkService.saveBookmark(member.getId(), recipe.getId())).willReturn(false);

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
    @CustomMockUser
    @DisplayName("즐겨찾기 해제 성공 테스트")
    void unBookmark_success_test() throws Exception {
        given(recipeBookmarkService.saveBookmark(member.getId(), recipe.getId())).willReturn(true);

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
    @DisplayName("레시피를 찾을 수 없는 경우 BadRequest 예외 발생 테스트")
    void BadRequest_Bookmark_test() throws Exception {
        given(recipeBookmarkService.saveBookmark(member.getId(), recipe.getId()))
                .willThrow(new NoSuchDataException(NoSuchErrorType.NO_SUCH_RECIPE));

        BookMarkRequest bookMarkRequest = new BookMarkRequest(recipe.getId());

        mockMvc.perform(post("/api/user/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMarkRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("레시피를 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @CustomMockUser
    @DisplayName("사용자가 즐겨찾기한 레시피 제목 페이징 조회 테스트")
    void bookmark_page() throws Exception {
        List<RecipeDto> list = List.of(
                RecipeDto.builder().id(1L).title("레시피1").build(),
                RecipeDto.builder().id(2L).title("레시피2").build(),
                RecipeDto.builder().id(3L).title("레시피3").build()
        );

        UserInfoBookmarkResponse userInfoBookmarkResponse = new UserInfoBookmarkResponse(false, list);
        given(recipeBookmarkService.userInfoBookmark(eq(memberId), isNull(), any(Pageable.class)))
                .willReturn(userInfoBookmarkResponse);

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
    @DisplayName("사용자 쿠키가 없는 상태에서 접근할 경우 Forbidden 예외 발생 테스트")
    void bookmark_page_NONECOOKIE() throws Exception {
        doThrow(new UnauthorizedException("잘못된 접근 이거나 일반 사용자만 가능합니다."))
                .when(cookieUtils).validCookie(isNull(), anyString());

        mockMvc.perform(get("/api/user/info/bookmark"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("잘못된 접근 이거나 일반 사용자만 가능합니다."));
    }

    @Test
    @CustomMockUser(id = 2L)
    @DisplayName("다른 사용자가 접근할 경우 데이터가 반환되지 않는 테스트")
    void other_user_access() throws Exception {
        List<RecipeDto> list = List.of(
                RecipeDto.builder().id(1L).title("레시피1").build(),
                RecipeDto.builder().id(2L).title("레시피2").build(),
                RecipeDto.builder().id(3L).title("레시피3").build()
        );

        UserInfoBookmarkResponse userInfoBookmarkResponse = new UserInfoBookmarkResponse(false, list);
        given(recipeBookmarkService.userInfoBookmark(eq(memberId), isNull(), any(Pageable.class)))
                .willReturn(userInfoBookmarkResponse);

        Cookie cookie = new Cookie("login-id", "cookie");
        mockMvc.perform(get("/api/user/info/bookmark")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @CustomMockUser
    @DisplayName("로그인한 사용자가 즐겨찾기한 상태를 조회하는 테스트")
    void loginIsBookmark() throws Exception {
        given(recipeBookmarkService.checkBookmark(eq(memberId), eq(recipeId))).willReturn(true);

        mockMvc.perform(get("/api/user/recipe/{recipeId}/bookmarks/check", recipeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @CustomMockUser(id = 2L)
    @DisplayName("로그인한 사용자가 즐겨찾기하지 않은 상태를 조회하는 테스트")
    void diff_loginIsBookmark() throws Exception {
        given(recipeBookmarkService.checkBookmark(eq(memberId), eq(recipeId))).willReturn(false);

        mockMvc.perform(get("/api/user/recipe/{recipeId}/bookmarks/check", recipeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("비로그인 사용자가 즐겨찾기 상태를 조회하는 테스트")
    void UnLoginIsBookmark() throws Exception {
        given(recipeBookmarkService.checkBookmark(isNull(), eq(recipeId))).willReturn(false);

        mockMvc.perform(get("/api/user/recipe/{recipeId}/bookmarks/check", recipeId))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
