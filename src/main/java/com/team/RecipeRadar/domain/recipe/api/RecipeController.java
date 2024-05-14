package com.team.RecipeRadar.domain.recipe.api;

import com.team.RecipeRadar.domain.recipe.application.RecipeBookmarkService;
import com.team.RecipeRadar.domain.recipe.application.RecipeService;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.global.Image.application.ImageService;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.Image.utils.FileStore;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Tag(name = "레시피 컨트롤러",description = "레피시 API")
@Slf4j
public class RecipeController {

    private final RecipeBookmarkService recipeBookmarkService;
    private final ImageService imageService;
    private final RecipeService recipeService;
    private final FileStore fileStore;

    @Operation(summary = "레시피 검색 API(무한 스크롤 방식)", description = "조회된 마지막 레시피의 Id값을 통해 다음페이지 여부를 판단 ('lastId'는 조회된 마지막 페이지 작성 값을 넣지않고 보내면 첫번째의 데이터만 출력 , page에 대한 쿼리스트링 작동 x)" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipeDtoList\":[{\"id\":128671,\"imageUrl\":\"링크.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"nextPage\":true}}")))
    })
    @GetMapping("/recipe")
    public ResponseEntity<?> findRecipe(@RequestParam("ingredients") List<String> ingredients, @RequestParam(value = "lastId",required = false)Long lastRecipeId, Pageable pageable){
        RecipeResponse recipeResponse = recipeService.searchRecipesByIngredients(ingredients,lastRecipeId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeResponse));
    }

    @Operation(summary = "레시피 검색 API(기본 페이징 방식)", description = "기본적인 페이지네이션 방식, sort는 사용안해도됩니다. 기본적으로 레시피를 오름차순 정렬 , Default.size = 10" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"content\":[{\"id\":128671,\"imageUrl\":\"https://recipe1.ezmember.co.kr/cache/recipe/2015/05/18/1fb83f8578488ba482ad400e3b62df49.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":1,\"unpaged\":false,\"paged\":true},\"last\":false,\"totalPages\":78221,\"totalElements\":78221,\"size\":1,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":1,\"empty\":false}}")))
    })
    @GetMapping("/recipeV1")
    public ResponseEntity<?> findRecipeV1(@RequestParam("ingredients") List<String> ingredients, Pageable pageable){
        Page<RecipeDto> recipeDtos = recipeService.searchRecipeByIngredientsNormal(ingredients, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeDtos));
    }


    @Operation(summary = "즐겨찾기 API",description = "사용자가 좋아요한 게시글의 대한 무한페이징 , 정렬은 기본적으로 서버에서 desc 순으로 설정하여 sort는 사용 x , 쿼리의 성능을 위해서 count쿼리는 사용하지않고" +
            "nextPage의 존재여부로 다음 페이지 호출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"성공 \",\"data\":{\"즐겨 찾기 상태\":\"[true or false]\"}]}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"사용자 및 레시피를 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "500",description = "SERVER ERROR",
                    content =@Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/user/recipe")
    public ResponseEntity<?> saveRecipeBookmark(@RequestBody BookMarkRequest bookMarkRequest){
        try {
            Boolean aBoolean = recipeBookmarkService.saveBookmark(Long.parseLong(bookMarkRequest.getMemberId()), bookMarkRequest.getRecipeId());
            Map<String,Boolean> bookMarkStatus = new LinkedHashMap<>();
            bookMarkStatus.put("즐겨 찾기 상태",aBoolean);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"성공",bookMarkStatus));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        } catch (Exception e){
            log.error("즐겨찾기 api 예외 발생");
            throw new ServerErrorException("서버 오류 발생");
        }
    }

    @Operation(summary = "레시피 정보 API", description = "해당 레시피의 자세한 정보를 보기위한 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipeDtoList\":{\"id\":128671,\"imageUrl\":\"링크.jpg\"," +
                                    "\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0},\"ingredients\":[\"어묵 2개\",\"재료 데이터\"]," +
                                    "\"cookStep\":[{\"cook_step_id\":\"193\",\"cook_steps\":\"당근과 양파는 깨끗히 씻으신 후에 채썰어 준비한 후 후라이팬에 기름을 두르고 팬을 달군 후 당근| 양파를 살짝 볶아주세요.\"}]}}")))
    })
    @GetMapping("/recipe/{id}")
    public ResponseEntity<?> getDetials(@PathVariable("id")Long recipe_id){
        RecipeDetailsResponse recipeDetails = recipeService.getRecipeDetails(recipe_id);

        return  ResponseEntity.ok(new ControllerApiResponse<>(true, "조회 성공",recipeDetails));
    }
    @Operation(summary = "레시피 좋아요순 조회", description = "좋아요가 많은 레시피의 대해서 8개만 출력하는 API 메인페이지에서 사용")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipe\":[\"8개의 레시피 데이터\"]}}")))
    })
    @GetMapping("/main/recipe")
    public ResponseEntity<?> mainRecipe(){
        MainPageRecipeResponse mainPageRecipeResponse = recipeService.mainPageRecipe();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",mainPageRecipeResponse));
    }


    @Operation(summary = "레시피 등록 API",description = "admin 권환을 가진 관리자만에 신규 레시피를 등록할수 있다.[이미지 파일은 최대 70MB이하까지만 가능하며, 확장자는 jpeg,jpg,png 만 등록가능]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"레시피 등록 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"[\\\"대표사진을 등록해주세요\\\" OR \\\"이미지 파일만 등록 해주세요\\\" OR \\\"70MB 이하로 등록해주세요\\\"]\"},{\"success\":false,\"message\":\"실패\",\"data\":{\"필드명\":\"필드 오류내용\"}}]"
                            ))),
            @ApiResponse(responseCode = "403",description = " Forbidden"),
            @ApiResponse(responseCode = "500",description = "SERVER ERROR",
                    content =@Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/admin/save/recipe", consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> recipe_image(@Valid @RequestPart RecipeSaveRequest recipeSaveRequest, BindingResult bindingResult, @RequestPart(required = false) MultipartFile file){
        try {
            if (bindingResult.hasErrors()){
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                Map<String,String> errors = new LinkedHashMap<>();
                for (FieldError error : fieldErrors){
                    errors.put(error.getField(),error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false,"실패",errors));
            }

            Recipe recipe = recipeService.saveRecipe(recipeSaveRequest);
            UploadFile uploadFile = fileStore.storeFile(file);
            imageService.saveRecipeImg(recipe,uploadFile);

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"레시피 등록 성공"));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());          //{"이미지 파일이 아닐시", "대표 이미지 사진이 등록 안될시","70MB 초과시"}
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }
}
