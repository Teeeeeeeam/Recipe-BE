package com.team.RecipeRadar.domain.like.api;

import com.team.RecipeRadar.domain.like.dto.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.ex.LikeException;
import com.team.RecipeRadar.domain.like.application.LikeService;
import com.team.RecipeRadar.domain.like.dto.PostLikeDto;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.ForbiddenException;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;


@RestController
@RequiredArgsConstructor
public class PostLikeController {

    @Qualifier("PostLikeServiceImpl")
    private final LikeService postLikeService;
    @Tag(name = "사용자 - 좋아요/즐겨찾기 컨트롤러", description = "좋아요/즐겨찾기 확인 및 처리")
    @Operation(summary = "게시글 - 좋아요",
            description = "로그인한 사용자만 가능 최초 요청 시 좋아요를 추가하고, 다시 요청하면 좋아요가 해제됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "[{\"success\" : true, \"message\" : \"좋아요 성공\"}, {\"success\" : false, \"message\" : \"좋아요 해제\"}]"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"[회원을 찾을 수가 없습니다.] or [게시물을 찾을 수없습니다.]\"}")))
    })
    @PostMapping("/api/user/postLike")
    public ResponseEntity<?> addLike(@RequestBody PostLikeDto postLikeDto){
        try {
            Boolean aBoolean = postLikeService.addLike(postLikeDto);
            ControllerApiResponse response;
            if (!aBoolean){
                response = new ControllerApiResponse(true,"좋아요 성공");
            }else
                response = new ControllerApiResponse(false, "좋아요 해제");
            return ResponseEntity.ok(response);
        }catch (NoSuchElementException e){
            e.printStackTrace();
            throw new LikeException(e.getMessage());
        }catch (Exception e){
            throw new ServerErrorException("서버 오류 발생");
        }
    }

    @Operation(summary = "게시글 - 좋아요 여부 확인",
            description = "로그인한 사용자가 해당 게시글을 좋아요 했는지 확인합니다. postId가 제공되지 않으면 false 값으로 응답됩니다.",tags = "사용자 - 좋아요/즐겨찾기 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "로그인한 사용자 요청시",
            content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
            examples = @ExampleObject(value = "{\"success\" : true, \"message\" : \"좋아요 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"[회원을 찾을 수가 없습니다.] or [게시물을 찾을 수없습니다.]\"}")))
    })
    @GetMapping("/api/likeCheck")
    public ResponseEntity<?> likeCheck(@Parameter(description = "게시글 Id") @RequestParam(value = "postId",required = false) String postId, HttpServletRequest request){
        try {
            String header = request.getHeader("Authorization");
            Boolean aBoolean = false;
            if (header!=null) {
                String jwtToken = header.replace("Bearer ", "");
              aBoolean = postLikeService.checkLike(jwtToken, Long.parseLong(postId));
            }
            return ResponseEntity.ok(new ControllerApiResponse(aBoolean,"좋아요 상태"));
        }catch (Exception e){
            throw new ServerErrorException("서버 오류 발생");
        }
    }

    @Operation(summary = "게시글 좋아요 내역(페이징)",description = "사용자가 좋아요한 게시글의 무한 페이징입니다.",tags = "사용자 - 마이페이지 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":\"boolean\",\"content\":[{\"id\":\"[게시글 id]\", \"content\" :\"[게시글 내용]\", \"title\":\"[게시글 제목]\"}]}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"해당 회원을 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401",description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"접근할 수 없는 사용자입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"쿠키값이 없을때 접근\"}"))),
            @ApiResponse(responseCode = "500",description = "SERVER ERROR",
                    content =@Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/api/user/info/{login-id}/posts/likes")
    public ResponseEntity<?> getUserLike(@PathVariable("login-id")String loginId,
                                         @RequestParam(value = "last-id",required = false)Long postLike_lastId,
                                         @CookieValue(name = "login-id",required = false) String cookieLoginId, Pageable pageable){
        try{
            if (cookieLoginId ==null){throw new ForbiddenException("쿠키값이 없을때 접근");}

            String authenticationName = getAuthenticationName();        //시큐리티 홀더에서 로그안한 사용자 정보 추출
            UserInfoLikeResponse userLikesByPage = postLikeService.getUserLikesByPage(authenticationName,loginId,postLike_lastId, pageable);

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",userLikesByPage));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        } catch (BadRequestException e){
            throw new AccessDeniedException(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    private static String getAuthenticationName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticationName = authentication.getName();
        return authenticationName;
    }
}
