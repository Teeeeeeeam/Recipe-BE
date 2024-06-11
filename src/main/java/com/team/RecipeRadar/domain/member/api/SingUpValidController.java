package com.team.RecipeRadar.domain.member.api;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dto.valid.*;
import com.team.RecipeRadar.domain.email.application.MailService;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
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

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "공용 - 회원가입 컨트롤러",description = "회원가입 및 검증 처리")
@RequestMapping("/api/join")
@Slf4j
public class SingUpValidController {

    private final MemberService memberService;

    @Qualifier("JoinEmail")
    private final MailService mailService;


    @Operation(summary = "로그인아이디 검증", description = "사용 가능한 아이디인지를 검증하는 API. 검증 조건은 대소문자를 포함한 5~16자의 문자열이며, 중복 여부도 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"사용 가능한 아이디\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"사용할수 없는 아이디입니다.\"}")))
    })
    @PostMapping("/register/validation")
    public ResponseEntity<ErrorResponse> LoginIdValid(@RequestBody LoginIdValidRequest loginIdValidRequest){
        try {
            Map<String, Boolean> stringBooleanMap = memberService.LoginIdValid(loginIdValidRequest.getLoginId());
            return ResponseEntity.ok(new ErrorResponse<>(stringBooleanMap.get("use_loginId"),"사용 가능한 아이디"));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }

    }

    @Operation(summary = "이메일 검증",description = "사용 가능한 이메일인지를 검증하는 API 가입할 수 있는 이메일 형식은 '이메일주소@도메인'이며, 'com' 또는 'net' 도메인만 허용됩니다. 이메일 중복 여부와 사용 가능 여부가 모두 true일 때에만 사용할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value ="{\"success\":true,\"message\":\"이메일 검증\",\"data\":{\"duplicateEmail\":\"boolean\",\"useEmail\":\"boolean\"}}")))
    })
    @PostMapping("/email/validation")
    public ResponseEntity<ControllerApiResponse> emailValid(@RequestBody EmailValidRequest emailValidRequest){
        try {

            Map<String, Boolean> stringBooleanMap = memberService.emailValid(emailValidRequest.getEmail());

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"이메일 검증", stringBooleanMap));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "이메일 인증번호 전송", description = "이메일 인증을 위한 인증 코드를 전송하는 API 이메일이 이미 가입된 경우에는 false가 반환됩니다. 단, 소셜 로그인은 이 기능에서 제외.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"메일 전송 성공\"}")))
    })
    @PostMapping("/email-confirmation")
    public ResponseEntity<?> mailConfirm(@Parameter(description = "이메일 주소") @RequestParam("email") String email){
        try {
            mailService.sensMailMessage(email);
            return ResponseEntity.ok(new ControllerApiResponse(true,"메일 전송 성공"));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "이메일 인증번호 검증",    description = "이메일 인증을 위한 인증 번호를 검증하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"이메일 검증 성공\",\"data\":{\"isVerifyCode\":true}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"인증번호가 일치하지 않습니다.\"}")))
    })
    @PostMapping("/email-confirmation/verify")
    public ResponseEntity<?> check(@Parameter(description = "이메일 주소")@RequestParam("email") String email
            ,@Parameter(description = "인증 번호")@RequestParam("code")String UserCode){
        try {
            Map<String, Boolean> stringBooleanMap = mailService.verifyCode(email,Integer.parseInt(UserCode));
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"이메일 검증 성공",stringBooleanMap));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }catch (NumberFormatException e){
            throw new BadRequestException("숫자만 입력해주세요");
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "닉네임 검증",description = "닉네임이 대소문자, 한글, 숫자로 이뤄진 4글자 이상이어야 하며 중복 여부도 검증가능. (true = 사용 가능, false = 사용 불가능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"닉네임 검증\",\"data\":true}")))
    })
    @PostMapping("/nickname/validation")
    public ResponseEntity<?> nickName(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples =  @ExampleObject(value = "{\"nickname\":\"닉네임\"}"))) @RequestBody Map<String,String> nickname){
        try {
            Boolean nicknameValid = memberService.nickNameValid(nickname.get("nickname")).get("nicknameValid");
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"닉네임 검증",nicknameValid));
        }catch (Exception e){
            throw new ServerErrorException("서버 오류");
        }
    }
}
