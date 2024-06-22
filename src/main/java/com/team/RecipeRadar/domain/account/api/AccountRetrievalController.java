package com.team.RecipeRadar.domain.account.api;

import com.team.RecipeRadar.domain.account.application.AccountRetrievalService;
import com.team.RecipeRadar.domain.account.dto.request.FindLoginIdRequest;
import com.team.RecipeRadar.domain.account.dto.request.FindPasswordRequest;
import com.team.RecipeRadar.domain.account.dto.request.UpdatePasswordRequest;
import com.team.RecipeRadar.global.utils.CookieUtils;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    private final CookieUtils cookieUtils;

    private final String ACCOUNT = "account-token";
    private final String LOGIN = "login-id";

    @Operation(summary = "아이디찾기",description = "사용자의 이름과 이메일을 통해 인증코드를 받아 아이디를 찾는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"성공\",\"data\":[{\"login_type\":\"normal\",\"login_info\":\"[로그인 아이디]\"},  {\"login_type\": \"naver\", \"login_info\": \"[소셜 로그인 아이디]\"}]}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[\"{\"success\":false,\"message\":\"실패\",\"data\":{\"필드명\" : \"필드 오류 내용\"}}\" , {\"success\":false,\"message\":\"인증번호가 일치하지 않습니다.\"}]")))
    })
    @PostMapping("/api/search/login-id")
    public ResponseEntity<?> test(@Valid @RequestBody FindLoginIdRequest findLoginIdRequest, BindingResult bindingResult){

        List<Map<String, String>> accountRetrievalServiceLoginId = accountRetrievalService.findLoginId(findLoginIdRequest.getUsername(), findLoginIdRequest.getEmail(),findLoginIdRequest.getCode());

        return ResponseEntity.ok(new ControllerApiResponse<>(true,"성공",accountRetrievalServiceLoginId));
    }

    @Operation(summary = "비밀번호 찾기",
            description = "사용자의 실명, 로그인 아이디, 이메일을 통해 인증코드를 받아 해당 사용자가 있는지 확인한 후, 모든 정보가 확인되면 Token 정보가 담긴 쿠키(account-token)을 발급합니다(유효시간 3분)." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[\"{\"success\":false,\"message\":\"실패\",\"data\":{\"필드명\" : \"필드 오류 내용\"}}\" , {\"success\":false,\"message\":\"인증번호가 일치하지 않습니다.\"}]"))),
    })
    @PostMapping("/api/search/password")
    public ResponseEntity<?> findPwd(@Valid @RequestBody FindPasswordRequest findPasswordRequest, BindingResult bindingResult){

        String token = accountRetrievalService.findPwd(findPasswordRequest.getUsername(), findPasswordRequest.getLoginId(), findPasswordRequest.getEmail(), findPasswordRequest.getCode());

        ResponseCookie responseCookie = cookieUtils.createCookie("account-token", token, 60 * 3);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(new ControllerApiResponse<>(true,"성공"));

    }

    @Operation(summary = "비밀번호 변경",description = "'account-token' 쿠키가 존재하면(비밀번호 찾기 후 비밀번호 변경), 'login-id' 쿠키가 존재하면(사용자 페이지에서 비밀번호 변경) 해당 엔드포인트에 접속하여 비밀번호를 변경할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"비밀번호 변경 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "[{\"success\":false,\"message\":\"실패\",\"data\":{\"필드명\" : \"필드 오류 내용\"}}, {\"success\":false,\"message\":\"[비밀번호가 일치하지 않습니다. OR  비밀번호가 안전하지 않습니다.]\"}]")))
    })
    @PutMapping("/api/password/update")
    public ResponseEntity<?>updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest, BindingResult bindingResult, HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        String accountId = "";
        String cookieId = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ACCOUNT) || cookie.getName().equals(LOGIN)) {
                    accountId = cookie.getValue();
                    cookieId = cookie.getName();
                    break;
                }
            }
        }

        accountRetrievalService.updatePassword(updatePasswordRequest,accountId);

        ResponseCookie deleteCookie = cookieUtils.deleteCookie(cookieId);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,deleteCookie.toString()).body(new ControllerApiResponse<>(true,"비밀번호 변경 성공"));
    }
}
