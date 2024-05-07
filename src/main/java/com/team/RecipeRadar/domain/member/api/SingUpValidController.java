package com.team.RecipeRadar.domain.member.api;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dto.valid.*;
import com.team.RecipeRadar.global.email.application.MailService;
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
@RequestMapping("/api/join")
@Tag(name = "회원가입 검증 Controller",description = "회원가입시 해당 필드값의 유효성 검증을 위한 요청 API")
@Slf4j
public class SingUpValidController {

    private final MemberService memberService;

    @Qualifier("JoinEmail")
    private final MailService mailService;


    @Operation(summary = "로그인아이디 검증", description = "사용가능한 아이디인지 검증(대문자나 소문자(5~16)자리)및 중복검사")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"사용 가능한 아이디\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"사용할수 없는 아이디입니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register/validation")
    public ResponseEntity<ErrorResponse> LoginIdValid(@RequestBody LoginIdValidRequest loginIdValidDto){
        try {
            Map<String, Boolean> stringBooleanMap = memberService.LoginIdValid(loginIdValidDto.getLoginId());
            return ResponseEntity.ok(new ErrorResponse<>(stringBooleanMap.get("use_loginId"),"사용 가능한 아이디"));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }

    }

    @Operation(summary = "이메일 검증", description = "사용 가능한 이메일인지 검증하는 API(이메일형식으로만 가입가능,com,net만 가능), duplicateEmail,useEmail이 모두 ture 일때만 사용 가능 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value ="{\"success\":true,\"message\":\"이메일 검증\",\"data\":{\"duplicateEmail\":\"boolean\",\"useEmail\":\"boolean\"}}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/email/validation")
    public ResponseEntity<ControllerApiResponse> emailValid(@RequestBody EmailValidRequest emailValidDto){
        try {

            Map<String, Boolean> stringBooleanMap = memberService.emailValid(emailValidDto.getEmail());

            return ResponseEntity.ok(new ControllerApiResponse<>(true,"이메일 검증", stringBooleanMap));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "이메일 인증번호 전송", description = "이메일 인증을 위한 인증코드 전송(일반 회원가입시 이미 가입된 이메일이있다면 false , 소셜로그인은 제외)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"메일 전송 성공\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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

    @Operation(summary = "이메일 인증번호 검증", description = "이메일 인증을 인증번호 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"이메일 검증 성공\",\"data\":{\"isVerifyCode\":true}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"인증번호가 일치하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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

    @Operation(summary = "닉네임 검증", description = "대소문자 한글 숫자로 이뤄진 4글자  이상이 닉네임이어야하며 중복된 닉네임인지 검증한다. (true = 사용가능 , false = 사용 불가능) ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"닉네임 검증\",\"data\":true}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/nickname/validation")
    public ResponseEntity<?> nickName(@RequestBody Map<String,String> nickname){
        try {
            Boolean nicknameValid = memberService.nickNameValid(nickname.get("nickname")).get("nicknameValid");
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"닉네임 검증",nicknameValid));
        }catch (Exception e){
            throw new ServerErrorException("서버 오류");
        }
    }
}
