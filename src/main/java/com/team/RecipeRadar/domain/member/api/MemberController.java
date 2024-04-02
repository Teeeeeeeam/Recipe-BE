package com.team.RecipeRadar.domain.member.api;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.advice.ApiControllerAdvice;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

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
//    private final ApplicationEventPublisher eventPublisher;


    @Operation(summary = "회원가입", description = "사용자가 회원가입합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"회원가입 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"globalError\": \"회원 가입시 모든 검사를 해주세요.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/singup")
    public ResponseEntity<?> join(@Valid @RequestBody MemberDto memberDto ,
                                  @Parameter(description = "이메일 인증 번호", required = true)@RequestParam("code") String code,
                                  BindingResult result){
        try{
            boolean validationOfSignUp = memberService.ValidationOfSignUp(memberDto,code);
            if (!validationOfSignUp){
                ObjectError error = new ObjectError("globalError", "회원 가입시 모든 검사를 해주세요");
                result.addError(error);
            }

            if (result.hasErrors()) {
                Map<String,String> errorMessage=new HashMap<>();
                for (FieldError error : result.getFieldErrors()) {
                    errorMessage.put(error.getField(),error.getDefaultMessage());
                }
                ObjectError globalError = result.getGlobalError();
                errorMessage.put(globalError.getObjectName(), globalError.getDefaultMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            Member member = memberService.saveDto(memberDto);
//            eventPublisher.publishEvent(new MemberJoinEmailEvent(member));
            return ResponseEntity.ok(new ControllerApiResponse(true,"회원가입 성공"));

        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류 발생");
        }

    }
}


