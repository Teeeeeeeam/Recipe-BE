package com.team.RecipeRadar.domain.email.api;

import com.team.RecipeRadar.domain.email.application.MailService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "공통 - 이메일 전송 컨트롤러",description = "이메일 인증 번호 전송 및 검증")
@RequiredArgsConstructor
@RequestMapping("/api/join")
public class SingUpEmailController {

    @Qualifier("JoinEmail")
    private final MailService mailService;

    @Operation(summary = "회원가입 이메일 인증번호 전송", description = "회원가입시 이메일 인증번호 전송 API 이메일이 이미 가입된 경우에는 false가 반환됩니다. 단, 소셜 로그인은 이 기능에서 제외.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"메일 전송 성공\"}")))
    })
    @PostMapping("/email-confirmation")
    public ResponseEntity<?> mailConfirm(@Parameter(description = "이메일 주소") @RequestParam("email") String email){
            mailService.sensMailMessage(email);
            return ResponseEntity.ok(new ControllerApiResponse(true,"메일 전송 성공"));
    }

    @Operation(summary = "회원가입 이메일 인증번호 검증",    description = "이메일 인증을 위한 인증 번호를 검증하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"이메일 검증 성공\",\"data\":{\"isVerifyCode\":true}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"[인증번호가 일치하지 않습니다. or 숫자만 입력해주세요.]\"}")))
    })
    @PostMapping("/email-confirmation/verify")
    public ResponseEntity<?> check(@Parameter(description = "이메일 주소")@RequestParam("email") String email,
                                   @Parameter(description = "인증 번호")@RequestParam("code")String UserCode){

            Map<String, Boolean> stringBooleanMap = mailService.verifyCode(email,Integer.parseInt(UserCode));
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"이메일 검증 성공",stringBooleanMap));
    }
}
