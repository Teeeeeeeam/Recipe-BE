package com.team.RecipeRadar.domain.member.api.user;

import com.team.RecipeRadar.domain.member.application.user.SinUpService;
import com.team.RecipeRadar.domain.member.dto.rqeust.JoinRequest;
import com.team.RecipeRadar.domain.member.dto.rqeust.EmailValidRequest;
import com.team.RecipeRadar.domain.member.dto.rqeust.LoginIdValidRequest;
import com.team.RecipeRadar.domain.member.dto.rqeust.NicknameValidRequest;
import com.team.RecipeRadar.global.payload.ErrorResponse;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "공용 - 회원가입 컨트롤러",description = "회원가입 및 검증 처리")
@RequestMapping("/api")
@Slf4j
public class SingUpValidController {

    private final SinUpService sinUpService;

    @Operation(summary = "회원가입", description = "사용자가 회원가입합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"회원가입 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false,\"message\":\"실패\",\"data\":{\"[필드명]\":\"[필드 오류 내용]\"}}"))),
    })
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequest joinRequest , BindingResult result){
        boolean validationOfSignUp = sinUpService.ValidationOfSignUp(JoinRequest.fromDto(joinRequest));

        if (!validationOfSignUp || result.hasErrors()){
            Map<String, String> map = sinUpService.ValidationErrorMessage(JoinRequest.fromDto(joinRequest));
            return getErrorResponseResponse(result,map);
        }

        sinUpService.joinMember(JoinRequest.fromDto(joinRequest));

        return ResponseEntity.ok(new ControllerApiResponse<>(true,"회원가입 성공"));
    }



    @Operation(summary = "로그인아이디 검증", description = "사용 가능한 아이디인지를 검증하는 API. 검증 조건은 대소문자를 포함한 5~16자의 문자열이며, 중복 여부도 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"사용 가능\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"사용할수 없는 아이디입니다.\"}")))
    })
    @PostMapping("/join/register/validation")
    public ResponseEntity<ControllerApiResponse> LoginIdValid(@RequestBody LoginIdValidRequest loginIdValidRequest){
        sinUpService.LoginIdValid(loginIdValidRequest.getLoginId());;
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
    @PostMapping("/join/email/validation")
    public ResponseEntity<ControllerApiResponse> emailValid(@RequestBody EmailValidRequest emailValidRequest){

        sinUpService.emailValidCon(emailValidRequest.getEmail());
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
    @PostMapping("/join/nickname/validation")
    public ResponseEntity<?> nickName(@RequestBody NicknameValidRequest nicknameValidRequest){
        sinUpService.nickNameValid(nicknameValidRequest.getNickname());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"닉네임 사용 가능"));
    }

    private static ResponseEntity<ErrorResponse<Map<String, String>>> getErrorResponseResponse(BindingResult bindingResult,Map<String, String> map) {
        Map<String, String> result = new LinkedHashMap<>();

        for(Map.Entry<String,String> entry : map.entrySet()){
            result.put(entry.getKey(),entry.getValue());
        }
        for (FieldError error : bindingResult.getFieldErrors()) {
            result.put(error.getField(),error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ErrorResponse<>(false, "실패", result));
    }
}
