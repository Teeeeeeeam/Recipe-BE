package com.team.RecipeRadar.domain.member.api;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dto.join.JoinRequest;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Slf4j
@Tag(name = "공용 - 회원가입 컨트롤러",description = "회원가입 및 검증 처리")
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

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
        boolean validationOfSignUp = memberService.ValidationOfSignUp(JoinRequest.fromDto(joinRequest));

        if (!validationOfSignUp || result.hasErrors()){
            Map<String, String> map = memberService.ValidationErrorMessage(JoinRequest.fromDto(joinRequest));
            return getErrorResponseResponse(result,map);
        }

        memberService.joinMember(JoinRequest.fromDto(joinRequest));

        return ResponseEntity.ok(new ControllerApiResponse<>(true,"회원가입 성공"));
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


