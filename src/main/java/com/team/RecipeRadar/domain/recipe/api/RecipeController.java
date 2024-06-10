package com.team.RecipeRadar.domain.recipe.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.recipe.application.RecipeBookmarkService;
import com.team.RecipeRadar.domain.recipe.application.RecipeService;
import com.team.RecipeRadar.domain.recipe.dto.*;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@Slf4j
public class RecipeController {

    private final RecipeBookmarkService recipeBookmarkService;
    private final RecipeService recipeService;
    private final S3UploadService s3UploadService;

    @Tag(name = "사용자 - 레시피 컨트롤러", description = "레시피 검색 및 관리")
    @Operation(summary = "레시피 검색(무한 스크롤)", description = "조회된 마지막 레시피의 ID 값을 통해 다음 페이지 여부를 판단합니다. 'lastId'는 조회된 마지막 페이지의 작성된 값을 넣지 않으면 첫 번째 데이터만 출력됩니다." ,tags ="사용자 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipeDtoList\":[{\"id\":128671,\"imageUrl\":\"링크.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"nextPage\":true}}")))
    })
    @GetMapping("/recipe")
    public ResponseEntity<?> findRecipe(@RequestParam("ingredients") List<String> ingredients,
                                        @RequestParam(value = "lastId",required = false)Long lastRecipeId, Pageable pageable){
        RecipeResponse recipeResponse = recipeService.searchRecipesByIngredients(ingredients,lastRecipeId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeResponse));
    }

    @Tag(name = "어드민 - 레시피 컨트롤러", description = "레시피 관리")
    @Operation(summary = "어드민 레시피 검색(무한 스크롤)", description = "조회된 마지막 레시피의 ID 값을 통해 다음 페이지 여부를 판단합니다. 'lastId'는 조회된 마지막 페이지의 작성된 값을 넣지 않으면 첫 번째 데이터만 출력됩니다.",tags ="어드민 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"recipeDtoList\":[{\"id\":128671,\"imageUrl\":\"링크.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"nextPage\":true}}")))
    })
    @GetMapping("/admin/recipe")
    public ResponseEntity<?> findRecipeWithAdmin(@RequestParam(value = "ingredients",required = false) List<String> ingredients,
                                                 @RequestParam(value = "title",required = false) String title,
                                                 @RequestParam(value = "lastId",required = false)Long lastRecipeId, Pageable pageable){
        RecipeResponse recipeResponse = recipeService.searchRecipesByTitleAndIngredients(ingredients,title,lastRecipeId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeResponse));
    }

    @Operation(summary = "레시피 검색 (기본 페이징)", description = "기본적인 페이지네이션 방식을 사용합니다. 기본적으로 레시피를 오름차순으로 정렬합니다." ,tags ="사용자 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"content\":[{\"id\":128671,\"imageUrl\":\"https://recipe1.ezmember.co.kr/cache/recipe/2015/05/18/1fb83f8578488ba482ad400e3b62df49.jpg\",\"title\":\"어묵김말이\",\"cookingLevel\":\"초급\",\"people\":\"2인분\",\"cookingTime\":\"60분이내\",\"likeCount\":0}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":1,\"unpaged\":false,\"paged\":true},\"last\":false,\"totalPages\":78221,\"totalElements\":78221,\"size\":1,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":1,\"empty\":false}}")))
    })
    @GetMapping("/recipeV1")
    public ResponseEntity<?> findRecipeV1(@RequestParam(value = "ingredients",required = false) List<String> ingredients,@RequestParam(value = "title", required = false) String title ,Pageable pageable){
        Page<RecipeDto> recipeDtos = recipeService.searchRecipeByIngredientsNormal(ingredients,title, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",recipeDtos));
    }


    @Operation(summary = "레시피 - 즐겨찾기",description = "레시피를 즐겨찾기 하는 API 즐겨찾기를 추가하면 true를 반환하고, 즐겨찾기를 해제하면 false를 반환합니다.",tags ="사용자 - 좋아요/즐겨찾기 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"성공\",\"data\":{\"즐겨 찾기 상태\":true}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"사용자 및 레시피를 찾을수 없습니다.\"}")))
    })
    @PostMapping("/user/recipe")
    public ResponseEntity<?> saveRecipeBookmark(@RequestBody BookMarkRequest bookMarkRequest){
        try {
            Boolean aBoolean = recipeBookmarkService.saveBookmark(bookMarkRequest.getMemberId(), bookMarkRequest.getRecipeId());
            Map<String,Boolean> bookMarkStatus = new LinkedHashMap<>();
            bookMarkStatus.put("즐겨 찾기 상태",aBoolean);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"성공",bookMarkStatus));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        } catch (Exception e){
            throw new ServerErrorException("서버 오류 발생");
        }
    }

    @Operation(summary = "레시피 상제 정보",description = "해당 레시피의 자세한 정보를 보기위한 API",tags ="사용자 - 레시피 컨트롤러")
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
    @Operation(summary = "레시피 좋아요순 조회",description = "좋아요가 많은 레시피 중 상위 8개를 출력하는 API",tags ="사용자 - 레시피 컨트롤러")
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


    @Operation(summary = "레시피 등록 API",description = "관리자만이 신규 레시피를 등록할 수 있는 API. 이미지 파일은 최대 10MB까지 가능하며, 확장자는 jpeg, jpg, png만 등록할 수 있습니다.",tags ="어드민 - 레시피 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"레시피 등록 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"[\\\"대표사진을 등록해주세요\\\" OR \\\"이미지 파일만 등록 해주세요\\\" OR \\\"70MB 이하로 등록해주세요\\\"]\"},{\"success\":false,\"message\":\"실패\",\"data\":{\"필드명\":\"필드 오류내용\"}}]"
                            )))
    })
    @PostMapping(value = "/admin/save/recipe", consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> recipe_save(@Valid @RequestPart RecipeSaveRequest recipeSaveRequest, BindingResult bindingResult,
                                                @RequestPart(required = false) MultipartFile file){
        try {
            if (bindingResult.hasErrors()){
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                Map<String,String> errors = new LinkedHashMap<>();
                for (FieldError error : fieldErrors){
                    errors.put(error.getField(),error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false,"실패",errors));
            }

            String uploadFile = s3UploadService.uploadFile(file);
            String originalFilename = file.getOriginalFilename();
            recipeService.saveRecipe(recipeSaveRequest,uploadFile,originalFilename);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"레시피 등록 성공"));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());          //{"이미지 파일이 아닐시", "대표 이미지 사진이 등록 안될시","10MB 초과시"}
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
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
    @PostMapping(value = "/admin/update/{recipe-id}",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateRecipe(@PathVariable(name = "recipe-id")Long recipeId ,
                                             @Valid @RequestPart RecipeUpdateRequest recipeUpdateRequest, BindingResult bindingResult,
                                             @RequestPart(required = false) MultipartFile file){
        try {
            if (bindingResult.hasErrors()){
                List<String> errors = new ArrayList<>();
                for (FieldError error : bindingResult.getFieldErrors()){
                    errors.add(error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false,"모든 값을 입력해주세요",errors));
            }
            recipeService.updateRecipe(recipeId,recipeUpdateRequest,file);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"레시피 수정 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        } catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }
    @Operation(summary = "레시피 - 즐겨찾기 상태 조회",
            description = "로그인하지 않은 사용자는 기본적으로 false를 반환하며, 로그인한 사용자는 해당 레시피의 즐겨찾기 여부에 따라 상태를 반환합니다.", tags ="사용자 - 좋아요/즐겨찾기 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "로그인한 사용자 요청시",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : true, \"message\" : \"즐겨찾기 상태\"}")))
    })
    @GetMapping("/check/bookmarks")
    public ResponseEntity<?> bookmarksCheck(@Parameter(description = "레시피 아이디") @RequestParam(value = "recipe-id",required = false) Long recipeId){
        try {
            Boolean isBookmark;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication instanceof AnonymousAuthenticationToken){
                isBookmark = false;
            }else {
                PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
                MemberDto memberDto = principal.getMemberDto(principal.getMember());
                Long member_Id = memberDto.getId();
                isBookmark = recipeBookmarkService.checkBookmark(member_Id, recipeId);
            }
            return ResponseEntity.ok(new ControllerApiResponse(isBookmark,"즐겨 찾기 상태"));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }
}
