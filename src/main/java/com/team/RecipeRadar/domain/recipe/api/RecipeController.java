package com.team.RecipeRadar.domain.recipe.api;

import com.team.RecipeRadar.domain.recipe.application.RecipeBookmarkService;
import com.team.RecipeRadar.domain.recipe.dto.BookMarkRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Tag(name = "레시피 컨트롤러",description = "레피시 API")
@Slf4j
public class RecipeController {

    private final RecipeBookmarkService recipeBookmarkService;


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
}
