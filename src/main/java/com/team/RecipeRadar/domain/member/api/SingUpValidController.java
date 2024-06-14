package com.team.RecipeRadar.domain.member.api;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dto.valid.*;
import com.team.RecipeRadar.global.exception.ErrorResponse;
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

@RestController
@RequiredArgsConstructor
@Tag(name = "공용 - 회원가입 컨트롤러",description = "회원가입 및 검증 처리")
@RequestMapping("/api/join")
@Slf4j
public class SingUpValidController {

    private final MemberService memberService;


    @Operation(summary = "로그인아이디 검증", description = "사용 가능한 아이디인지를 검증하는 API. 검증 조건은 대소문자를 포함한 5~16자의 문자열이며, 중복 여부도 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"사용 가능\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"사용할수 없는 아이디입니다.\"}")))
    })
    @PostMapping("/register/validation")
    public ResponseEntity<ControllerApiResponse> LoginIdValid(@RequestBody LoginIdValidRequest loginIdValidRequest){
        memberService.LoginIdValid(loginIdValidRequest.getLoginId());;
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"사용 가능"));
    }

    @Operation(summary = "이메일 검증",description = "사용 가능한 이메일인지를 검증하는 API 가입할 수 있는 이메일 형식은 '이메일주소@도메인'이며, 'com' 또는 'net' 도메인만 허용됩니다. 이메일 중복 여부와 사용 가능 여부가 모두 true일 때에만 사용할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"이메일 사용 가능\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"이메일 사용 불가능\"}")))
    })
    @PostMapping("/email/validation")
    public ResponseEntity<ControllerApiResponse> emailValid(@RequestBody EmailValidRequest emailValidRequest){

        memberService.emailValidCon(emailValidRequest.getEmail());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"이메일 사용 가능"));
    }

    @Operation(summary = "닉네임 검증",description = "닉네임이 대소문자, 한글, 숫자로 이뤄진 4글자 이상이어야 하며 중복 여부도 검증가능. (true = 사용 가능, false = 사용 불가능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"닉네임 사용 가능\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"사용 불가능한 닉네임 입니다.\"}")))
    })
    @PostMapping("/nickname/validation")
    public ResponseEntity<?> nickName(@RequestBody NicknameValidRequest nicknameValidRequest){
        memberService.nickNameValid(nicknameValidRequest.getNickName());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"닉네임 사용 가능"));
    }
}
