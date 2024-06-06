package com.team.RecipeRadar.domain.inquiry.api;

import com.team.RecipeRadar.domain.inquiry.application.InquiryService;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserAddRequest;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserUpdateRequest;
import com.team.RecipeRadar.domain.inquiry.dto.user.ValidInquiryRequest;
import com.team.RecipeRadar.domain.inquiry.exception.InquiryException;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@OpenAPIDefinition(tags = {
        @Tag(name = "일반 사용자 문의사항 컨트롤러", description = "일반 사용자 문의사항 작업")
})
@Slf4j
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "문의사항 작성 API", description = "로그인한 사용자만 문의사항 작성 가능", tags = {"일반 사용자 문의사항 컨트롤러"} )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"작성 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples =  @ExampleObject(value = "{\"success\": false, \"message\": \"모든 값을 입력해 주세요\"}}"))),
    })
    @PostMapping("/api/user/inquires")
    public ResponseEntity<?> inquiryAdd(@Valid @RequestBody UserAddRequest userAddInquiryDto, BindingResult bindingResult) {
        try {
            ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
            if (errorMap != null) return errorMap;
            inquiryService.save(userAddInquiryDto);
            return ResponseEntity.ok(new ControllerApiResponse(true,"작성 성공"));
        }catch (NoSuchElementException e){
            throw new InquiryException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "문의사항 삭제 API",description = "작성한 사용자만 문의사항 삭제가능",tags = {"일반 사용자 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"문의사항 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"문의사항을 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"작성자만 삭제할수 있습니다.\"}")))

    })
    @DeleteMapping("/api/user/inquires/{inquiry-id}")
    public ResponseEntity<?> deleteInquiry(@PathVariable("inquiry-id") Long inquiryId) {
        try{
            String loginId = authenticationLogin();
            inquiryService.delete(loginId,inquiryId);
            return ResponseEntity.ok(new ControllerApiResponse(true,"문의사항 삭제 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());         //예외처리-> 여기서 처리안하고  @ExceptionHandler로 예외처리함
        }
    }

    @Operation(summary = "문의사항 수정 API", description = "로그인한 사용자만 수정이 가능하며 작성자만 수정이 가능하도록 이전에 비밀번호 검증을 통해서 검증확인해 해당 API 접근가능", tags = {"일반 사용자 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"문의사항 수정 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"문의사항을 찾을수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"작성자만 수정할수 있습니다.\"}")))
    })
    @PostMapping("/api/user/update/inquiries/{inquiry-id}")
    public  ResponseEntity<?> updateInquiry(@Valid @RequestBody UserUpdateRequest updateInquiryDto, BindingResult bindingResult,@PathVariable("inquiry-id") Long inquiryId){
        try{
            ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
            if (errorMap != null) return errorMap;

            String loginId = authenticationLogin();
            inquiryService.update(inquiryId,updateInquiryDto,loginId);

            return ResponseEntity.ok(new ControllerApiResponse(true,"문의사항 수정 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }

    @Operation(summary = "문의사항 비밀번호 검증 API",description = "문의사항 삭제 수정시 해당 메소드를 통해 게시글 작성시 입력한 비밀번호의 대해서 검증 성공시에만 수정,삭제가 가능하도록",tags = {"일반 사용자 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"비밀번호 인증 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"비밀번호가 일치하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"작성한 사용자만 가능합니다.\"}")))

    })
    @PostMapping("/api/valid/inquiries")
    public ResponseEntity<?> validInquiry(@RequestBody ValidInquiryRequest request){
        try {
            String login = authenticationLogin();
            boolean valid = inquiryService.validInquiryPassword(login, request);
            return ResponseEntity.ok(new ControllerApiResponse<>(valid,"비밀번호 인증 성공"));
        }catch (BadRequestException e){
            throw new BadRequestException(e.getMessage());
        }
    }

    //로그인한 사용자의 loginId를 스프링 시큐리티에서 획득
    private static String authenticationLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String loginId = principal.getMemberDto(principal.getMember()).getLoginId();
        return loginId;
    }

    /*
    BindingResult 의 예외 Valid 여러곳의 사용되어서 메소드로 추출
     */
    private static ResponseEntity<ErrorResponse<Map<String, String>>> getErrorResponseResponseEntity(BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            Map<String,String> errorMap = new HashMap<>();
            for(FieldError error : bindingResult.getFieldErrors()){
                errorMap.put(error.getField(),error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(new ErrorResponse<>(false, "모든 값을 입력해 주세요", errorMap));
        }
        return null;
    }
}
