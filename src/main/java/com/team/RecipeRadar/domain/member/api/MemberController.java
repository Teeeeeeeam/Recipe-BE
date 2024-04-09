package com.team.RecipeRadar.domain.member.api;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.member.dto.UserInfoResponse;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.advice.ApiControllerAdvice;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "회원가입 컨트롤러",description = "회원가입을 하기위한 API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;


    @Operation(summary = "회원가입", description = "사용자가 회원가입합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"회원가입 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"실패\",\"data\":{\"[필드명]\":\"[필드 오류 내용]\", \"globalError\": \"모든 검사를 검증해주세요\"}} , {\"success\":false,\"message\":\"인증번호가 일치하지 않습니다.\"}]"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody MemberDto memberDto , BindingResult result,
                                  @Parameter(description = "이메일 인증 번호", required = true)@RequestParam("code") String code
                                 ){
        try{
            boolean validationOfSignUp = memberService.ValidationOfSignUp(memberDto,Integer.parseInt(code));
            if (!validationOfSignUp){
                ObjectError error = new ObjectError("globalError", "모든 검사를 검증해주세요");
                result.addError(error);
            }

            if (result.hasErrors()) {

                Map<String,String> errorMessage=new HashMap<>();
                for (FieldError error : result.getFieldErrors()) {
                    errorMessage.put(error.getField(),error.getDefaultMessage());
                }

                ObjectError globalError = result.getGlobalError();

                errorMessage.put(globalError.getObjectName(), globalError.getDefaultMessage());
                ErrorResponse<Object> errorResponse = ErrorResponse.builder()
                        .success(false)
                        .message("실패")
                        .data(errorMessage).build();

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

             memberService.saveDto(memberDto);

            ControllerApiResponse<Object> response = ControllerApiResponse.builder()
                    .success(true)
                    .message("회원가입 성공").build();
            return ResponseEntity.ok(response);

        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }catch (NumberFormatException e){
            throw new BadRequestException("숫자만 입력해주세요");
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류 발생");
        }
    }

    @Operation(summary = "회원정보 조회", description = "회원의 회원정보(이름,닉네임,이메일,로그인타입)을 조회 한다.",tags={"사용자 페이지 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"조회성공\", \"data\": {\"username\": \"홍길동\", \"nickName\":\"홍길동\", \"email\":\"test@naver.com\",\"loginType\":\"normal\" }}"))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"잘못된 접근입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/user/info/{login-id}")
    public ResponseEntity<?> userInfo(@PathVariable("login-id")String loginId){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();

            UserInfoResponse members = memberService.getMembers(loginId,name);

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회성공",members));
        }catch (AccessDeniedException e){
            throw new AccessDeniedException(e.getMessage());
        }catch (ServerErrorException e){
            throw new ServerErrorException(e.getMessage());
        }

    }
}


