package com.team.RecipeRadar.domain.member.api;


import com.team.RecipeRadar.domain.member.application.AccountRetrievalService;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.FindLoginIdDto;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.FindPasswordDto;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.UpdatePasswordDto;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.email.application.MailService;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

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

    /*
    아이디 찾기시 사용되는 엔드포인트
     */


    @Operation(summary = "아이디찾기", description = "사용자의 이름과 이메일을통해 인증코드를 통한 아이디찾기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": [{\"로그인 타입\": \"normal\", \"로그인 정보\": \"[로그인 아이디]\"}, {\"로그인 타입\": \"naver\", \"로그인 정보\": \"[소셜 로그인 아이디]\"}]}"))),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/loginid/find")
    public ResponseEntity<?> test(@RequestBody FindLoginIdDto findLoginIdDto){
        try {
            String username = findLoginIdDto.getUsername();
            String email = findLoginIdDto.getEmail();
            String code = findLoginIdDto.getCode();

            List<Map<String, String>> loginId = accountRetrievalService.findLoginId(username, email,code);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,loginId));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("오류발생");
        }
    }

    @Operation(summary = "비밀번호 찾기", 
            description = "사용자실명, 로그인아이디, 이메일을 통한인증코드를 통해서 해당 사용자가 있는지 확인후 모두 true이며 token을 발급해준다. 그후 /api/pwd/update로 라디이렉트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": [{\"token\": \"[생성된 토큰값]\", \"회원 정보\": \"true\"}, {\"이메일 인증\": \"true\"}]}"))),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/pwd/find")
    public ResponseEntity<?> findPwd(@RequestBody FindPasswordDto findPasswordDto){
        try {
            Map<String, Object> pwd = accountRetrievalService.findPwd(findPasswordDto.getUsername(), findPasswordDto.getLoginId(), findPasswordDto.getEmail(),findPasswordDto.getCode());
            return ResponseEntity.ok(new ControllerApiResponse<>(true,pwd));
        }catch (Exception e){
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
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"[비밀번호가 일치하지 않습니다. OR  비밀번호가 안전하지 안습니다.]\"}"))),
            @ApiResponse(responseCode = "500", description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/api/pwd/update")
    public ResponseEntity<?>updatePassword(@Parameter(description = "비밀번호 찾기시 생성된 TOKEN 값")@RequestParam String id ,@RequestBody UpdatePasswordDto updatePasswordDto){
        try {
            ControllerApiResponse apiResponse = accountRetrievalService.updatePassword(updatePasswordDto,id);
            return ResponseEntity.ok(apiResponse);
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ControllerApiResponse(false,e.getMessage()));
        }
        catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ControllerApiResponse(false, "잘못된 접근"));
        }
        catch (Exception e){
            throw new ServerErrorException("서버 오류");
        }
    }

    

    @Operation(summary = "찾기 메일전송 API",description = "이메일 찾기시 사용되는 이메일전송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"인증번호\": \"[요청된 인증번호]\"}}"))),
            @ApiResponse(responseCode = "500",description = "SERVER ERROR",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/find/mailConfirm")
    public ResponseEntity<?> mailConfirm(@Parameter(description ="이메일") @RequestParam("email") String email){
        try {

            Map<String,String> result= new LinkedHashMap<>();
            String code = mailService.sensMailMessage(email);
            result.put("인증번호",code);

            return ResponseEntity.ok(new ControllerApiResponse<>(true,result));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "인증코드 검증",description = "인증번호가 일치하는지 검증하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"isVerifyCode\": \"[true]\"}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\": \"인증번호가 일치하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "500",description = "SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/api/find/mailConfirm/check")
    public ResponseEntity<?> check(@RequestParam("code")String UserCode){
        try {
            Map<String, Boolean> stringBooleanMap = mailService.verifyCode(UserCode);
            return ResponseEntity.ok(new ControllerApiResponse<>(true,stringBooleanMap));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }
}
