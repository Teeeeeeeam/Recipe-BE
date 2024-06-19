package com.team.RecipeRadar.domain.recipe.api.admin;

import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.domain.recipe.application.admin.AdminRecipeService;
import com.team.RecipeRadar.domain.recipe.dto.response.RecipeResponse;
import com.team.RecipeRadar.domain.recipe.dto.request.RecipeSaveRequest;
import com.team.RecipeRadar.domain.recipe.dto.request.RecipeUpdateRequest;
import com.team.RecipeRadar.global.payload.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminRecipeController {

    private final AdminRecipeService adminService;
    private final S3UploadService s3UploadService;

    @Operation(summary = "레시피수 조회", description = "작성된 레시피의 수를 조회하는 API",tags = "어드민 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\":\"조회 성공\", \"data\":\"10\"}"))),
    })
    @GetMapping("/recipes/count")
    public ResponseEntity<?> getAllRecipesCount(){
        long searchAllMembers = adminService.searchAllRecipes();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",searchAllMembers));
    }


    @Tag(name = "어드민 - 레시피 컨트롤러", description = "레시피 관리")
    @Operation(summary = "어드민 레시피 검색(무한 스크롤)", description = "조회된 마지막 레시피의 ID 값을 통해 다음 페이지 여부를 판단합니다. 'lastId'는 조회된 마지막 페이지의 작성된 값을 넣지 않으면 첫 번째 데이터만 출력됩니다.",tags ="어드민 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipeDtoList\":[{\"id\":128671,\"imageUrl\":\"링크.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"nextPage\":true}}")))
    })
    @GetMapping("/recipes")
    public ResponseEntity<?> findRecipeWithAdmin(@RequestParam(value = "ingredients",required = false) List<String> ingredients,
                                                 @RequestParam(value = "title",required = false) String title,
                                                 @RequestParam(value = "lastId",required = false)Long lastRecipeId,
                                                 @Parameter(example = "{\"size\":10}") Pageable pageable){
        RecipeResponse recipeResponse = adminService.searchRecipesByTitleAndIngredients(ingredients,title,lastRecipeId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeResponse));
    }


    @Operation(summary = "레시피 삭제",description = "레시피를 단일, 일괄 삭제하는 API",tags = "어드민 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"레시피 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"해당 레시피를 찾을수 없습니다.\"}"))),

    })
    @DeleteMapping("/recipes")
    public ResponseEntity<?> deleteRecipe(@RequestParam(value = "recipeIds") List<Long> recipeIds){
        adminService.deleteRecipe(recipeIds);
        return ResponseEntity.ok(new ControllerApiResponse(true,"레시피 삭제 성공"));
    }

    @Operation(summary = "레시피 등록 API",description = "관리자만이 신규 레시피를 등록할 수 있는 API. 이미지 파일은 최대 10MB까지 가능하며, 확장자는 jpeg, jpg, png만 등록할 수 있습니다.",tags ="어드민 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"레시피 등록 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"[\\\"대표사진을 등록해주세요\\\" OR \\\"이미지 파일만 등록 해주세요\\\" OR \\\"70MB 이하로 등록해주세요\\\"]\"},{\"success\":false,\"message\":\"실패\",\"data\":[\"필드 오류내용\"]}]")))
    })
    @PostMapping(value = "/save/recipes", consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> recipe_save(@Valid @RequestPart RecipeSaveRequest recipeSaveRequest, BindingResult bindingResult,
                                         @RequestPart MultipartFile file){

        if (bindingResult.hasErrors())
            return getErrorResponse(bindingResult);

        adminService.saveRecipe(recipeSaveRequest,file);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"레시피 등록 성공"));
    }


    @Operation(summary = "레시피 수정 API",description = "관리자만이 기존 레시피를 수정할 수 있는 API 수정 시 좋아요 수를 제외한 모든 정보를 수정할 수 있으며, 빈 칸으로는 등록할 수 없습니다.", tags ="어드민 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"레시피 수정 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\": false, \"message\": \"모든 값을 입력해주세요\", \"data\": {\"errors\": [\"변경할 레시피의 제목를 입력해주세요\"]}}, {\"success\": false, \"message\": \"해당 레시피를 찾을수 없습니다.\"}]"))),
    })
    @PutMapping(value = "/update/{recipeId}",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRecipe(@Schema(example = "400648")@PathVariable(name = "recipeId")Long recipeId ,
                                          @Valid @RequestPart RecipeUpdateRequest recipeUpdateRequest, BindingResult bindingResult,
                                          @RequestPart(required = false) MultipartFile file){
        if (bindingResult.hasErrors())
            return getErrorResponse(bindingResult);
        adminService.updateRecipe(recipeId,recipeUpdateRequest,file);

        return ResponseEntity.ok(new ControllerApiResponse<>(true,"레시피 수정 성공"));
    }

    private static ResponseEntity<ErrorResponse<List<String>>> getErrorResponse(BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : bindingResult.getFieldErrors()){
            errors.add(error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse<>(false, "모든 값을 입력해주세요", errors));
    }
}
