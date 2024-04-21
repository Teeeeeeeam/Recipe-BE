package com.team.RecipeRadar.domain.member.api;


import com.team.RecipeRadar.domain.member.application.AccountRetrievalService;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.FindLoginIdRequest;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.FindPasswordRequest;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.UpdatePasswordRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@Slf4j
@Tag(name = "아이디찾기 및 비밀번호 찾기 컨트롤러", description = "개인정보를 찾기 위한 API")
@RequiredArgsConstructor
public class AccountRetrievalController {


    private final AccountRetrievalService accountRetrievalService;
    @Qualifier("AccountEmail")
    private final MailService mailService;

    @Operation(summary = "아이디찾기", description = "사용자의 이름과 이메일을통해 인증코드를 통한 아이디찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"성공\",\"data\":[{\"로그인 타입\":\"normal\",\"로그인 정보\":\"[로그인 아이디]\"},  {\"로그인 타입\": \"naver\", \"로그인 정보\": \"[소셜 로그인 아이디]\"}]}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"실패\",\"data\":{\"[필드명]\":\"[필드 오류 내용]\"}} , {\"success\":false,\"message\":\"인증번호가 일치하지 않습니다.\"}]"))),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
            description = "사용자실명, 로그인아이디, 이메일을 통한인증코드를 통해서 해당 사용자가 있는지 확인후 모두 true이며 token을 발급해준다. 그후 /api/pwd/update로 라디이렉트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"성공\",\"data\":{\"token\":\"[토큰 값]\",\"회원 정보\":true,\"이메일 인증\":true}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"실패\",\"data\":{\"[필드명]\":\"[필드 오류 내용]\"}} , {\"success\":false,\"message\":\"인증번호가 일치하지 않습니다.\"}]"))),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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

            ControllerApiResponse<Object> response = ControllerApiResponse.builder()
                    .success(true)
                    .message("성공")
                    .data(pwd).build();

            return ResponseEntity.ok(response);
        } catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "비밀번호 변경",description = "토큰값을 받아, 해당 토큰이 존재한다면 해당 앤드포인트에 접속이 가능해 비밀번호 변경이 가능")
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
    public ResponseEntity<?>updatePassword(@Parameter(description = "비밀번호 찾기시 생성된 TOKEN 값")@RequestParam String id , @Valid @RequestBody UpdatePasswordRequest updatePasswordDto, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()){
                Map<String, String> result = new LinkedHashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    result.put(error.getField(),error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false,"실패", result));
            }
            ControllerApiResponse apiResponse = accountRetrievalService.updatePassword(updatePasswordDto,id);
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

    

    @Operation(summary = "찾기 메일전송 API",description = "이메일 찾기시 사용되는 이메일전송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"메일 전송 성공\"}"))),
            @ApiResponse(responseCode = "500",description = "SERVER ERROR",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/search/email-confirmation/send")
    public ResponseEntity<?> mailConfirm(@Parameter(description ="이메일") @RequestParam("email") String email){
        try {
            mailService.sensMailMessage(email);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,"메일 전송 성공"));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "인증코드 검증",description = "인증번호가 일치하는지 검증하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"성공\" ,\"data\" : {\"isVerifyCode\": \"true\"}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"인증번호가 일치하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "500",description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
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
