package com.team.RecipeRadar.domain.member.api;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dto.valid.*;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import java.util.LinkedHashMap;
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
    public ResponseEntity<ErrorResponse> LoginIdValid(@RequestBody LoginIdValidDto loginIdValidDto){
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

//    @Operation(summary = "비밀번호 일치 검증", description = "비밀번호가 서로 일치하는지 검증하는 API(먼저 강력한 비밀번호 검증 성공후 입력가능)")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
//                            examples = @ExampleObject(value = "{\"duplicate_password\": \"[true] or [false].\"}"))),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/api/members/checkPasswordDuplication")
//    public ResponseEntity<Map<String,Boolean>> duplicatePassword(@RequestBody PasswordDuplicatedDto passwordDuplicatedDto){
//        try{
//            Map<String, Boolean> stringBooleanMap = memberService.duplicatePassword(passwordDuplicatedDto.getPassword(),passwordDuplicatedDto.getPasswordRe());
//            return ResponseEntity.ok(stringBooleanMap);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerErrorException("서버오류");
//        }
//
//    }
//    @Operation(summary = "사용자 비밀번호 강력도 검증", description = "입력한 비밀번호가 규칙에 맞게 사용된 비밀번호인지 확인(\"특수문자\",\"0~9\",\"소문자\",\"대문자\",\"8자리이상\")")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
//                            examples = @ExampleObject(value = "{\"passwordStrength\": \"[true] or [false].\"}"))),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/api/members/checkPasswordStrength")
//    public ResponseEntity<Map<String,Boolean>> checkPasswordStrength(@RequestBody PasswordStrengthDto passwordStrengthDto){
//        try {
//            Map<String, Boolean> stringBooleanMap = memberService.checkPasswordStrength(passwordStrengthDto.getPassword());
//            return ResponseEntity.ok(stringBooleanMap);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerErrorException("서버오류");
//
//        }
//    }
//
//    @Operation(summary = "사용자 닉네임 검증", description = "사용가능 닉네임인지 검증(한글,영어,숫자 4글자 이상")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
//                            examples = @ExampleObject(value = "{\"nickNameValid\": \"[true] or [false].\"}"))),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/api/members/checkNickNameValidity")
//    public ResponseEntity<Map<String,Boolean>> nickNameValid(@RequestBody NicknameValidDto usernameValidDto){
//        try {
//            Map<String, Boolean> stringBooleanMap = memberService.nickNameValid(usernameValidDto.getNickName());
//            return ResponseEntity.ok(stringBooleanMap);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerErrorException("서버오류");
//        }
//    }
//
//    @Operation(summary = "사용자 실명 검증", description = "사용 가능한 실명인지 검증(한글 2글자 이상)")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
//                            examples = @ExampleObject(value = "{\"isKorean\": \"[true] or [false].\"}"))),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/api/members/checkUserNameValidity")
//    public ResponseEntity<Map<String,Boolean>> userNameValid(@RequestBody UsernameValidDto usernameValidDto){
//        try {
//            Map<String, Boolean> stringBooleanMap = memberService.userNameValid(usernameValidDto.getUsername());
//            return ResponseEntity.ok(stringBooleanMap);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new ServerErrorException("서버오류");
//        }
//    }

    @Operation(summary = "이메일 검증", description = "사용 가능한 이메일인지 검증하는 API(이메일형식으로만 가입가능. com/net만 사용가능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value ="{\"duplicateEmail\": \"[true] or [false].\", \"useEmail\" : \"true\"}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/email/validation")
    public ResponseEntity<Map<String,Boolean>> emailValid(@RequestBody EmailValidDto emailValidDto){
        try {

            Map<String, Boolean> stringBooleanMap = memberService.emailValid(emailValidDto.getEmail());
            return ResponseEntity.ok(stringBooleanMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }
    
//    @Operation(summary = "모든 필드값 검증", description = "모든 필드값의 유효성 검증을 체크후 true 가 나온다면  회원가입 앤드포인트로 회원가입 진행")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK",
//                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
//                            examples = @ExampleObject(value = "{\"isValidateSignUp\": \"[true] or [false].\"}"))),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    })
//    @PostMapping("/api/members/validateSignUp")
//    public ResponseEntity<Map<String,Boolean>> ValidationOfSignUp(@RequestBody MemberDto memberDto, @Parameter(description = "인증 번호")@RequestParam String code){
//        try {
//            Map<String, Boolean> map = new LinkedHashMap<>();
//            boolean valid = memberService.ValidationOfSignUp(memberDto,Integer.parseInt(code));
//            map.put("isValidateSignUp", valid);
//            return ResponseEntity.ok(map);
//        }catch (BadRequestException e){
//            throw new BadRequestException(e.getMessage());
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            throw new ServerErrorException("서버오류");
//        }
//    }

    @Operation(summary = "이메일 인증번호 전송", description = "이메일 인증을 위한 인증코드 전송(일반 회원가입시 이미 가입된 이메일이있다면 false , 소셜로그인은 제외)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"인증번호\": \"[요청된 인증번호]\"}}"))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/email-confirmation")
    public ResponseEntity<?> mailConfirm(@Parameter(description = "이메일 주소") @RequestParam("email") String email){
        try {
            String code = mailService.sensMailMessage(email);

            return ResponseEntity.ok(new ControllerApiResponse(true,code));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "이메일 인증번호 검증", description = "이메일 인증을 인증번호 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"isVerifyCode\": \"[true]\"}}"))),
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
            return ResponseEntity.ok(new ControllerApiResponse<>(true,stringBooleanMap));
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
}
