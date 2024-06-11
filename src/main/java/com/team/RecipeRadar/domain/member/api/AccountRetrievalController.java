package com.team.RecipeRadar.domain.member.api;


import com.team.RecipeRadar.domain.admin.domain.BlackListRepository;
import com.team.RecipeRadar.domain.member.application.AccountRetrievalService;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.FindLoginIdRequest;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.FindPasswordRequest;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.UpdatePasswordRequest;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@Tag(name = "공용 - 아이디 및 비밀번호 찾기 컨트롤러", description = "계정 찾기")
@RequiredArgsConstructor
public class AccountRetrievalController {


    private final AccountRetrievalService accountRetrievalService;
    private final BlackListRepository blackListRepository;
    @Qualifier("AccountEmail")
    private final MailService mailService;

    @Operation(summary = "아이디찾기",description = "사용자의 이름과 이메일을 통해 인증코드를 받아 아이디를 찾는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"성공\",\"data\":[{\"login_type\":\"normal\",\"login_info\":\"[로그인 아이디]\"},  {\"login_type\": \"naver\", \"login_info\": \"[소셜 로그인 아이디]\"}]}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"실패\",\"data\":{\"[필드명]\":\"[필드 오류 내용]\"}} , {\"success\":false,\"message\":\"인증번호가 일치하지 않습니다.\"}]")))
    })
    @PostMapping("/api/search/login-id")
    public ResponseEntity<?> test(@Valid @RequestBody FindLoginIdRequest findLoginIdRequest, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()){
                Map<String, String> result = new LinkedHashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    result.put(error.getField(),error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false,"실패",result));
            }
            String username = findLoginIdRequest.getUsername();
            String email = findLoginIdRequest.getEmail();
            Integer code = findLoginIdRequest.getCode();

            List<Map<String, String>> loginId = accountRetrievalService.findLoginId(username, email,code);

            ControllerApiResponse<Object> response = ControllerApiResponse.builder()
                    .success(true)
                    .message("성공")
                    .data(loginId).build();

            return ResponseEntity.ok(response);
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("오류발생");
        }
    }

    @Operation(summary = "비밀번호 찾기",
            description = "사용자의 실명, 로그인 아이디, 이메일을 통해 인증코드를 받아 해당 사용자가 있는지 확인한 후, 모든 정보가 확인되면 Token 정보가 담긴 쿠키(account-token)을 발급합니다(유효시간 3분)." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"성공\",\"data\":{\"회원 정보\":true,\"이메일 인증\":true}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"실패\",\"data\":{\"[필드명]\":\"[필드 오류 내용]\"}} , {\"success\":false,\"message\":\"인증번호가 일치하지 않습니다.\"}]"))),
    })
    @PostMapping("/api/search/password")
    public ResponseEntity<?> findPwd(@Valid @RequestBody FindPasswordRequest findPasswordDto, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()){
                Map<String, String> result = new LinkedHashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    result.put(error.getField(),error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false,"실패",result));
            }
            Map<String, Object> pwd = accountRetrievalService.findPwd(findPasswordDto.getUsername(), findPasswordDto.getLoginId(), findPasswordDto.getEmail(),findPasswordDto.getCode());

            String token = (String) pwd.get("token");

            ResponseCookie accountToken = ResponseCookie.from("account-token", token)
                    .maxAge(60 * 3)
                    .secure(true)
                    .httpOnly(true)
                    .path("/")
                    .sameSite("None")
                    .build();
            pwd.remove("token");

            ControllerApiResponse<Object> response = ControllerApiResponse.builder()
                    .success(true)
                    .message("성공")
                    .data(pwd).build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,accountToken.toString()).body(response);
        } catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "비밀번호 변경",description = "'account-token' 쿠키가 존재하면(비밀번호 찾기 후 비밀번호 변경), 'login-id' 쿠키가 존재하면(사용자 페이지에서 비밀번호 변경) 해당 엔드포인트에 접속하여 비밀번호를 변경할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"비밀번호 변경 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"실패\",\"data\":{\"[필드명]\":\"[필드 오류 내용]\"}} , {\"success\":false,\"message\":\"[비밀번호가 일치하지 않습니다. OR  비밀번호가 안전하지 않습니다.]\"}]"))),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/api/password/update")
    public ResponseEntity<?>updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordDto, BindingResult bindingResult, HttpServletRequest request){
        try {
            if (bindingResult.hasErrors()){
                Map<String, String> result = new LinkedHashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    result.put(error.getField(),error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false,"실패", result));
            }
            Cookie[] cookies = request.getCookies();
            String accountId = "";
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("account-token")){
                    accountId = cookie.getValue();
                }if(cookie.getName().equals("login-id")){
                    accountId = cookie.getValue();
                }
            }
            ControllerApiResponse apiResponse = accountRetrievalService.updatePassword(updatePasswordDto,accountId);
            return ResponseEntity.ok(apiResponse);
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse<>(false,e.getMessage()));
        }
        catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse<>(false, "잘못된 접근"));
        }
        catch (Exception e){
            throw new ServerErrorException("서버 오류");
        }
    }

    

    @Operation(summary = "계정 찾기 메일 전송",description = "이메일 찾기 시 사용되는 이메일 전송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"메일 전송 성공\"}")))
    })
    @PostMapping("/api/search/email-confirmation/send")
    public ResponseEntity<?> mailConfirm(@Parameter(description ="이메일") @RequestParam("email") String email){
        try {
            boolean existsByEmail = blackListRepository.existsByEmail(email);
            if(!existsByEmail) {
                mailService.sensMailMessage(email);
                return ResponseEntity.ok(new ControllerApiResponse<>(true, "메일 전송 성공"));
            }else return ResponseEntity.badRequest().body(new ErrorResponse<>(false,"사용할수 없는 이메일입니다."));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "인증코드 검증",description = "인증번호가 일치하는지를 검증하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"성공\" ,\"data\" : {\"isVerifyCode\": \"true\"}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"인증번호가 일치하지 않습니다.\"}")))
    })
    @PostMapping("/api/search/email-confirmation/check")
    public ResponseEntity<?> check(@RequestParam("email")String email, @RequestParam("code")String userCode){
        try {
            
            Map<String, Boolean> stringBooleanMap = mailService.verifyCode(email,Integer.parseInt(userCode));
            ControllerApiResponse<Object> response = ControllerApiResponse.builder()
                    .success(true)
                    .message("성공")
                    .data(stringBooleanMap).build();
            
            return ResponseEntity.ok(response);
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }catch (NumberFormatException e){
            throw new BadRequestException("숫자만 입력해주세요");
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }
}
