package com.team.RecipeRadar.domain.recipe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.application.RecipeBookmarkService;
import com.team.RecipeRadar.domain.recipe.application.RecipeServiceImpl;
import com.team.RecipeRadar.domain.recipe.domain.CookingStep;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.global.Image.application.ImgServiceImpl;
import com.team.RecipeRadar.global.Image.utils.FileStore;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockAdmin;
import com.team.mock.CustomMockUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
@Slf4j
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RecipeBookmarkService recipeBookmarkService;
    @MockBean RecipeServiceImpl recipeService;
    @MockBean FileStore fileStore;
    @MockBean ImgServiceImpl imgService;
    @MockBean S3UploadService s3UploadService;


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

        BookMarkRequest bookMarkRequest = new BookMarkRequest(1l, recipe.getId());

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

        BookMarkRequest bookMarkRequest = new BookMarkRequest(1l, recipe.getId());

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

        BookMarkRequest bookMarkRequest = new BookMarkRequest(1l, recipe.getId());

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
        List<Map<String,String>> cookSetp = new ArrayList<>();

        List<CookingStep> cookingStepList = List.of(CookingStep.builder().id(1l).steps("조리1").build(), CookingStep.builder().id(2l).steps("조리2").build(),
                CookingStep.builder().id(3l).steps("조리3").build());

        for (CookingStep cookingStep : cookingStepList) {
            Map<String,String> map = new LinkedHashMap<>();
            map.put("cook_step_id", String.valueOf(cookingStep.getId()));
            map.put("cook_steps", cookingStep.getSteps());
            cookSetp.add(map);
        }

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

    @Test
    @DisplayName("재료 검색 레시피 조회 일반 페이지 네이션 테스트_제목으로만 검색")
    void Search_Recipe_Normal_Page() throws Exception {

        List<String> ingredients = Arrays.asList("밥");
        Pageable pageRequest = PageRequest.of(0, 2);

        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1L, "url1", "레시피1", "level1", "1인분", "10분", 0));
        recipeDtoList.add(new RecipeDto(2L, "url2", "레시피1", "level2", "2인분", "1시간", 0));


        PageImpl<RecipeDto> dtoPage = new PageImpl<>(recipeDtoList, pageRequest, 2);

        given(recipeService.searchRecipeByIngredientsNormal(isNull(),eq("레시피1"),any(Pageable.class)))
                .willReturn(dtoPage);

        mockMvc.perform(get("/api/recipeV1?title=레시피1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.[0].id").value(1))
                .andExpect(jsonPath("$.data.content.[0].imageUrl").value("url1"))
                .andExpect(jsonPath("$.data.content.[0].title").value("레시피1"))
                .andExpect(jsonPath("$.data.content.[0].cookingLevel").value("level1"))
                .andExpect(jsonPath("$.data.content.[0].people").value("1인분"))
                .andExpect(jsonPath("$.data.content.[0].cookingTime").value("10분"))
                .andExpect(jsonPath("$.data.content.size()").value(2));
    }
    @Test
    @DisplayName("재료 검색 레시피 조회 일반 페이지 네이션 테스트_재료으로만 검색")
    void Search_Recipe_Normal_Page_ing() throws Exception {

        List<String> ingredients = Arrays.asList("밥");
        Pageable pageRequest = PageRequest.of(0, 2);

        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1L, "url1", "레시피1", "level1", "1인분", "10분", 0));
        recipeDtoList.add(new RecipeDto(2L, "url2", "레시피1", "level2", "2인분", "1시간", 0));


        PageImpl<RecipeDto> dtoPage = new PageImpl<>(recipeDtoList, pageRequest, 2);

        given(recipeService.searchRecipeByIngredientsNormal(eq(ingredients),isNull(),any(Pageable.class)))
                .willReturn(dtoPage);

        mockMvc.perform(get("/api/recipeV1?ingredients=밥")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.[0].id").value(1))
                .andExpect(jsonPath("$.data.content.[0].imageUrl").value("url1"))
                .andExpect(jsonPath("$.data.content.[0].title").value("레시피1"))
                .andExpect(jsonPath("$.data.content.[0].cookingLevel").value("level1"))
                .andExpect(jsonPath("$.data.content.[0].people").value("1인분"))
                .andExpect(jsonPath("$.data.content.[0].cookingTime").value("10분"))
                .andExpect(jsonPath("$.data.content.size()").value(2));
    }

    @Test
    @DisplayName("메인 페이지에서 레시피 좋아요순으로 출력")
    void main_Page_Recipe_like_desc() throws Exception {
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1l, "url", "레시피1", "level1", "1", "10minute", 16));
        recipeDtoList.add(new RecipeDto(2l, "url", "레시피2", "level2", "2", "1hour", 13));
        recipeDtoList.add(new RecipeDto(3l, "url", "레시피3", "level2", "3", "1hour", 3));
        MainPageRecipeResponse of = MainPageRecipeResponse.of(recipeDtoList);

        given(recipeService.mainPageRecipe()).willReturn(of);

        mockMvc.perform(get("/api/main/recipe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipe.size()").value(3))
                .andExpect(jsonPath("$.data.recipe.[0].likeCount").value(16))
                .andExpect(jsonPath("$.data.recipe.[1].likeCount").value(13))
                .andExpect(jsonPath("$.data.recipe.[2].likeCount").value(3));


    }

    @Test
    @DisplayName("레시피 등록 테스트 (어드민 등록 성공시)")
    @CustomMockAdmin
    void save_Recipe_Admin_Success() throws Exception {
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        String uploadFile= "test.jpg";
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        given(s3UploadService.uploadFile(eq(mockMultipartFile))).willReturn(uploadFile);
        doNothing().when(recipeService).saveRecipe(eq(recipeSaveRequest),anyString(),eq(uploadFile));

        MockMultipartFile request = new MockMultipartFile("recipeSaveRequest", null, "application/json", objectMapper.writeValueAsString(recipeSaveRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/api/admin/save/recipe")
                                .file(mockMultipartFile)
                                .file(request)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("레시피 등록 성공"));
    }

    @Test
    @DisplayName("레시피 등록 테스트 (일반 사용자 등록 실패)")
    @CustomMockUser
    void save_Recipe_User_Fail() throws Exception {
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps);
        String uploadFile= "test.jpg";
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        given(s3UploadService.uploadFile(eq(mockMultipartFile))).willReturn(uploadFile);
        doNothing().when(recipeService).saveRecipe(eq(recipeSaveRequest), anyString(),eq(uploadFile));

        MockMultipartFile request = new MockMultipartFile("recipeSaveRequest", null, "application/json", objectMapper.writeValueAsString(recipeSaveRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/api/admin/save/recipe")
                                .file(mockMultipartFile)
                                .file(request)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("레시피 등록 테스트 (파일명확장자가 아닐시 오류)")
    @CustomMockAdmin
    void save_Recipe_With_Invalid_FileName() throws Exception {
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.AAA", "image/jpeg", "test data".getBytes());

        given(s3UploadService.uploadFile(eq(mockMultipartFile))).willThrow(new BadRequestException("이미지 파일만 등록해 주세요"));

        MockMultipartFile request = new MockMultipartFile("recipeSaveRequest", null, "application/json", objectMapper.writeValueAsString(recipeSaveRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/api/admin/save/recipe")
                                .file(mockMultipartFile)
                                .file(request)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미지 파일만 등록해 주세요"));
    }

    @Test
    @DisplayName("레시피 등록 테스트 (대표 이미지 등록 안했을 때)")
    @CustomMockAdmin
    void save_Empty_File() throws Exception {
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", null, "image/jpeg", "test data".getBytes());

        given(s3UploadService.uploadFile(eq(mockMultipartFile))).willThrow(new BadRequestException("대표 이미지를 등록해 주세요"));

        MockMultipartFile request = new MockMultipartFile("recipeSaveRequest", null, "application/json", objectMapper.writeValueAsString(recipeSaveRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/api/admin/save/recipe")
                                .file(mockMultipartFile)
                                .file(request)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("대표 이미지를 등록해 주세요"));
    }
    @Test
    @CustomMockAdmin
    @DisplayName("레시피 수정 성공시 테스트")
    void recipe_update_successful() throws Exception {
        String originFileName = "test.jpg";
        long recipe_id =1l;
        List<String> newCook = List.of("새로운 값~~");
        List<Long> delete = List.of(1l);

        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest("제목", "난이도", "인원수",
                List.of("재료1", "재료2"), "조리시간", List.of(Map.of("cook_step_id", "1", "cook_steps", "1번째 조리순서"), Map.of("cook_step_id", "2", "cook_steps", "2번째 조리순서")),newCook,delete);

        MockMultipartFile multipartFile = new MockMultipartFile("file", originFileName, "image/jpeg", "controller test".getBytes());
        doNothing().when(recipeService).updateRecipe(eq(recipe_id),eq(recipeUpdateRequest),eq(multipartFile));

        MockMultipartFile recipeUpdateRequest_multi = new MockMultipartFile("recipeUpdateRequest", null, "application/json", objectMapper.writeValueAsString(recipeUpdateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(multipart("/api/admin/update/"+recipe_id)
                        .file(recipeUpdateRequest_multi)
                        .file(multipartFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("레시피 수정 성공"));
    }

    @Test
    @CustomMockAdmin
    @DisplayName("레시피 수정시 Valid 발생 테스트")
    void recipe_update_fail_valid() throws Exception {
        String originFileName = "test.jpg";
        long recipe_id =1l;
        List<String> newCook = List.of("새로운 값~~");
        List<Long> delete = List.of(1l);

        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest("", "난이도", "인원수",
                List.of("", "재료2"), "조리시간", List.of(Map.of("cook_step_id", "1", "cook_steps", "1번째 조리순서"), Map.of("cook_step_id", "", "cook_steps", "2번째 조리순서")),newCook,delete);

        MockMultipartFile multipartFile = new MockMultipartFile("file", originFileName, "image/jpeg", "controller test".getBytes());
        doNothing().when(recipeService).updateRecipe(eq(recipe_id),eq(recipeUpdateRequest),eq(multipartFile));

        MockMultipartFile recipeUpdateRequest_multi = new MockMultipartFile("recipeUpdateRequest", null, "application/json", objectMapper.writeValueAsString(recipeUpdateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(multipart("/api/admin/update/"+recipe_id)
                        .file(recipeUpdateRequest_multi)
                        .file(multipartFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("모든 값을 입력해주세요"))
                .andExpect(jsonPath("$.data.[0]").isString())
                .andExpect(jsonPath("$.data.[1]").isString());
    }

    @Test
    @CustomMockAdmin
    @DisplayName("레시피 수정시 레시피가 존재하지 않을때 테스트")
    void recipe_update_noEmpty_recipe() throws Exception {
        String originFileName = "test.jpg";
        long recipe_id =1l;
        List<String> newCook = List.of("새로운 값~~");
        List<Long> delete = List.of(1l);
        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest("제목", "난이도", "인원수",
                List.of("재료1", "재료2"), "조리시간", List.of(Map.of("cook_step_id", "1", "cook_steps", "1번째 조리순서"), Map.of("cook_step_id", "조리순서", "cook_steps", "2번째 조리순서")),newCook,delete);
        
        MockMultipartFile multipartFile = new MockMultipartFile("file", originFileName, "image/jpeg", "controller test".getBytes());

        doThrow(new NoSuchElementException("해당 레시피를 찾을수가 없습니다.")).when(recipeService).updateRecipe(recipe_id,recipeUpdateRequest,multipartFile);

        MockMultipartFile recipeUpdateRequest_multi = new MockMultipartFile("recipeUpdateRequest", null, "application/json", objectMapper.writeValueAsString(recipeUpdateRequest).getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(multipart("/api/admin/update/"+recipe_id)
                        .file(recipeUpdateRequest_multi)
                        .file(multipartFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("해당 레시피를 찾을수가 없습니다."));
    }


    @Test
    @CustomMockAdmin
    @DisplayName("어드민 검색 레시피 조회 테스트")
    void Search_Recipe1() throws Exception {

        Pageable pageRequest = PageRequest.of(0, 2);

        String title= "recipe";
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        recipeDtoList.add(new RecipeDto(1L, "url1", title, "level1", "1인분", "10분", 0));

        boolean paged = pageRequest.next().isPaged();

        RecipeResponse recipeResponse = new RecipeResponse(recipeDtoList, paged);

        given(recipeService.searchRecipesByTitleAndIngredients(isNull(), eq(title), isNull(), any(Pageable.class)))
                .willReturn(recipeResponse);

        mockMvc.perform(get("/api/admin/recipe?title="+title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recipeDtoList.[0].id").value(1))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].imageUrl").value("url1"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].title").value(title))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].cookingLevel").value("level1"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].people").value("1인분"))
                .andExpect(jsonPath("$.data.recipeDtoList.[0].cookingTime").value("10분"))
                .andExpect(jsonPath("$.data.recipeDtoList.size()").value(1));
    }

    @Test
    @CustomMockUser
    @DisplayName("로그인한 사용자 즐겨찾기 상태")
    void loginIsBookmark() throws Exception {
        Long recipeId = 1L;
        Long memberId = 1L;

        given(recipeBookmarkService.checkBookmark(eq(memberId), eq(recipeId))).willReturn(true);

        mockMvc.perform(get("/api/check/bookmarks")
                        .param("recipe-id", recipeId.toString()))
                .andDo(print())
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

        mockMvc.perform(get("/api/check/bookmarks")
                        .param("recipe-id", recipeId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    @DisplayName("비로그인 사용자 즐겨찾기 상태")
    void UnLoginIsBookmark() throws Exception {
        Long recipeId = 1L;

        given(recipeBookmarkService.checkBookmark(isNull() ,eq(recipeId))).willReturn(false);

        mockMvc.perform(get("/api/check/bookmarks")
                        .param("recipe-id", recipeId.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}