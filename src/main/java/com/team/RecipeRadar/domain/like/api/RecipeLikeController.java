package com.team.RecipeRadar.domain.like.api;

import com.team.RecipeRadar.domain.like.ex.LikeException;
import com.team.RecipeRadar.domain.like.application.LikeService;
import com.team.RecipeRadar.domain.like.dto.RecipeLikeDto;
import com.team.RecipeRadar.global.exception.ErrorResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Tag(name = "레시피 좋아요 컨트롤러",description = "사용자가 레시피 좋아요 API")
@Slf4j
public class RecipeLikeController {

    @Qualifier("RecipeLikeServiceImpl")
    private final LikeService postLikeService;


    @Operation(summary = "좋아요를 API",
            description = "로그인한 사용자만 좋아요를 할수있으며, 기본값으로는 좋아여가 되어 있지않다. 최초 요청시 좋아요가 되며 좋아요가된 상태에서 다시 요청을하면 좋아요를 해제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "[{\"success\" : true, \"message\" : \"좋아요 성공\"}, {\"success\" : true, \"message\" : \"좋아요 해제\"}]"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"[회원을 찾을 수가 없습니다.] or [게시물을 찾을 수없습니다.]\"}")))
    })
    @PostMapping("/api/user/recipe/like")
    public ResponseEntity<?> addLike(@RequestBody RecipeLikeDto recipeLikeDto){
        try {
            Boolean aBoolean = postLikeService.addLike(recipeLikeDto);
            ControllerApiResponse response;
            if (!aBoolean){
                response = new ControllerApiResponse(true,"좋아요 성공");
            }else
                response = new ControllerApiResponse(true, "좋아요 해제");
            return ResponseEntity.ok(response);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            throw new LikeException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }

    @Operation(summary = "좋아요를 했는지 확인",
            description = "로그인한 사용자가 해당 게시글을 좋아요 했는지 확인하는 API recipeId 'required = false' 로 설정해 비사용자는 모든 필드값을 좋아요하지않은 상태로 보여준다." +
                    " \n(사용자 검증시 로그인후 사용, 비사용자 요청시에는 success=false로 응답)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "로그인한 사용자 요청시",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : true, \"message\" : \"좋아요 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"[회원을 찾을 수가 없습니다.] or [게시물을 찾을 수없습니다.]\"}")))
    })

    @GetMapping("/api/recipe/like/check/{recipe-id}")
    public ResponseEntity<?> likeCheck(@Parameter(description = "레시피 Id") @PathVariable(value = "recipe-id",required = false) String recipeId, HttpServletRequest request){
        try {
            String header = request.getHeader("Authorization");
            Boolean aBoolean = false;
            if (header!=null) {
                String jwtToken = header.replace("Bearer ", "");
                aBoolean = postLikeService.checkLike(jwtToken, Long.parseLong(recipeId));
            }
            return ResponseEntity.ok(new ControllerApiResponse(aBoolean,"좋아요 상태"));
        }catch (Exception e){
            throw new ServerErrorException("서버 오류 발생");
        }
    }
}
