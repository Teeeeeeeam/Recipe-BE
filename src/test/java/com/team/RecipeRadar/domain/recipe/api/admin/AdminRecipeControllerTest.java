package com.team.RecipeRadar.domain.recipe.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.domain.recipe.application.admin.AdminRecipeService;
import com.team.RecipeRadar.domain.post.application.user.PostServiceImpl;
import com.team.RecipeRadar.domain.recipe.domain.type.CookIngredients;
import com.team.RecipeRadar.domain.recipe.domain.type.CookMethods;
import com.team.RecipeRadar.domain.recipe.domain.type.DishTypes;
import com.team.RecipeRadar.domain.recipe.dto.request.RecipeSaveRequest;
import com.team.RecipeRadar.domain.recipe.dto.request.RecipeUpdateRequest;
import com.team.RecipeRadar.global.aop.AspectAdvice;
import com.team.RecipeRadar.global.aop.Pointcuts;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.RecipeRadar.global.exception.ex.img.ImageErrorType;
import com.team.RecipeRadar.global.exception.ex.img.ImageException;
import com.team.RecipeRadar.domain.email.event.ResignEmailHandler;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.mock.CustomMockAdmin;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityTestConfig.class, Pointcuts.class,AspectAdvice.class})
@WebMvcTest(AdminRecipeController.class)
@ImportAutoConfiguration(AopAutoConfiguration.class)
class AdminRecipeControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean ApplicationEventPublisher eventPublisher;
    @MockBean ResignEmailHandler resignEmailHandler;
    @MockBean ApplicationEvent applicationEvent;
    @MockBean AdminRecipeService adminService;
    @MockBean PostServiceImpl postService;
    @MockBean S3UploadService s3UploadService;
    private ObjectMapper objectMapper = new ObjectMapper();




    @Test
    @DisplayName("레시피 수 전제 조회")
    @CustomMockAdmin
    void getRecipe_count() throws Exception {
        long count =101111;
        given(adminService.searchAllRecipes()).willReturn(count);


        mockMvc.perform(get("/api/admin/recipes/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(count));
    }

    @Test
    @DisplayName("레시피 등록 테스트 (어드민 등록 성공시)")
    @CustomMockAdmin
    void save_Recipe_Admin_Success() throws Exception {
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        String uploadFile= "test.jpg";
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps, CookIngredients.BEEF, CookMethods.SASHIMI, DishTypes.BREAD);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        given(s3UploadService.uploadFile(eq(mockMultipartFile),anyList())).willReturn(uploadFile);
        doNothing().when(adminService).saveRecipe(eq(recipeSaveRequest),eq(mockMultipartFile));

        MockMultipartFile request = new MockMultipartFile("recipeSaveRequest", null, "application/json", objectMapper.writeValueAsString(recipeSaveRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/api/admin/save/recipes")
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
    @CustomMockAdmin
    @DisplayName("레시피 수정 성공시 테스트")
    void recipe_update_successful() throws Exception {
        String originFileName = "test.jpg";
        long recipe_id =1l;
        List<String> newCook = List.of("새로운 값~~");
        List<Long> delete = List.of(1l);

        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest("제목", "난이도", "인원수",
                List.of("재료1", "재료2"), "조리시간", List.of(Map.of("cook_step_id", "1", "cook_steps", "1번째 조리순서"), Map.of("cook_step_id", "2", "cook_steps", "2번째 조리순서")),newCook,delete,CookIngredients.BEEF,CookMethods.SASHIMI,DishTypes.BREAD);

        MockMultipartFile multipartFile = new MockMultipartFile("file", originFileName, "image/jpeg", "controller test".getBytes());
        doNothing().when(adminService).updateRecipe(eq(recipe_id),eq(recipeUpdateRequest),eq(multipartFile));

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
    @CustomMockUser
    @DisplayName("레시피 등록 테스트 (일반 사용자 등록 실패)")
    void save_Recipe_User_Fail() throws Exception {
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps,CookIngredients.BEEF, CookMethods.SASHIMI, DishTypes.BREAD);
        String uploadFile= "test.jpg";
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        given(s3UploadService.uploadFile(eq(mockMultipartFile),anyList())).willReturn(uploadFile);
        doNothing().when(adminService).saveRecipe(eq(recipeSaveRequest), eq(mockMultipartFile));

        MockMultipartFile request = new MockMultipartFile("recipeSaveRequest", null, "application/json", objectMapper.writeValueAsString(recipeSaveRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/api/admin/save/recipe")
                                .file(mockMultipartFile)
                                .file(request)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(401));
    }

    @Test
    @CustomMockAdmin
    @DisplayName("레시피 수정시 Valid 발생 테스트")
    void recipe_update_fail_valid() throws Exception {
        String originFileName = "test.jpg";
        long recipe_id =1l;
        List<String> newCook = List.of("새로운 값~~");
        List<Long> delete = List.of(1l);

        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest("","", "",
                List.of("", "재료2"), "조리시간", List.of(Map.of("cook_step_id", "1", "cook_steps", "1번째 조리순서"), Map.of("cook_step_id", "", "cook_steps", "2번째 조리순서")),newCook,delete,CookIngredients.BEEF,CookMethods.SASHIMI,DishTypes.BREAD);

        MockMultipartFile multipartFile = new MockMultipartFile("file", originFileName, "image/jpeg", "controller test".getBytes());
        doNothing().when(adminService).updateRecipe(eq(recipe_id),eq(recipeUpdateRequest),eq(multipartFile));

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
                .andExpect(jsonPath("$.message").value("실패"))
                .andExpect(jsonPath("$.data.size()").value(4));
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
                List.of("재료1", "재료2"), "조리시간", List.of(Map.of("cook_step_id", "1", "cook_steps", "1번째 조리순서"), Map.of("cook_step_id", "조리순서", "cook_steps", "2번째 조리순서")),newCook,delete,CookIngredients.BEEF,CookMethods.SASHIMI,DishTypes.BREAD);

        MockMultipartFile multipartFile = new MockMultipartFile("file", originFileName, "image/jpeg", "controller test".getBytes());

        doThrow(new NoSuchDataException(NO_SUCH_RECIPE)).when(adminService).updateRecipe(recipe_id,recipeUpdateRequest,multipartFile);

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
                .andExpect(jsonPath("$.message").value("레시피를 찾을 수 없습니다."));
    }
    @Test
    @DisplayName("레시피 등록 테스트 (파일명확장자가 아닐시 오류)")
    @CustomMockAdmin
    void save_Recipe_With_Invalid_FileName() throws Exception {
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps,CookIngredients.BEEF, CookMethods.SASHIMI, DishTypes.BREAD);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.AAA", "image/jpeg", "test data".getBytes());

        doThrow(new ImageException(ImageErrorType.INVALID_IMAGE_FORMAT)).when(adminService).saveRecipe(any(),any());

        MockMultipartFile request = new MockMultipartFile("recipeSaveRequest", null, "application/json", objectMapper.writeValueAsString(recipeSaveRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                        multipart("/api/admin/save/recipes")
                                .file(mockMultipartFile)
                                .file(request)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미지 형식이 잘못되었습니다"));
    }

    @Test
    @DisplayName("레시피 등록 테스트 (대표 이미지 등록 안했을 때)")
    @CustomMockAdmin
    void save_Empty_File() throws Exception {
        List<String> ingredients = List.of("재료1", "재료2");
        List<String> cooksteps = List.of("조리1", "조리2");
        RecipeSaveRequest recipeSaveRequest = new RecipeSaveRequest("title", "초급", "인원수", ingredients, "시간", cooksteps,CookIngredients.BEEF, CookMethods.SASHIMI, DishTypes.BREAD);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", null, "image/jpeg", "test data".getBytes());

        MockMultipartFile request = new MockMultipartFile("recipeSaveRequest", null, "application/json", objectMapper.writeValueAsString(recipeSaveRequest).getBytes(StandardCharsets.UTF_8));
        doThrow(new ImageException(ImageErrorType.MISSING_PRIMARY_IMAGE)).when(adminService).saveRecipe(any(),any());

        mockMvc.perform(
                        multipart("/api/admin/save/recipes")
                                .file(mockMultipartFile)
                                .file(request)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("대표 이미지를 등록해주세요"));
    }
}