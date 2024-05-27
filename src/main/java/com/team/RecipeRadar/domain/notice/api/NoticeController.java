package com.team.RecipeRadar.domain.notice.api;

import com.team.RecipeRadar.domain.notice.application.NoticeService;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminUpdateRequest;
import com.team.RecipeRadar.domain.notice.dto.admin.NoticeDetailResponse;
import com.team.RecipeRadar.domain.notice.dto.admin.NoticeResponse;
import com.team.RecipeRadar.domain.notice.dto.info.AdminInfoNoticeResponse;
import com.team.RecipeRadar.domain.notice.exception.NoticeException;
import com.team.RecipeRadar.domain.notice.exception.ex.NoticeNotFoundException;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.ForbiddenException;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@OpenAPIDefinition(tags = {
        @Tag(name = "관리자 공지사항 컨트롤러", description = "관리자 공지사항 작업")
})
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 작성 API", description = "관리자만 공지사항 작성 가능", tags = {"공지사항 컨트롤러"} )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"작성 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples =  @ExampleObject(value = "{\"success\": false, \"message\": \"모든 값을 입력해 주세요\"}}"))),
    })
    @PostMapping(value = "/api/admin/notices", consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> noticeAdd(@Valid @RequestPart AdminAddRequest adminAddNoticeDto, BindingResult bindingResult, @RequestPart MultipartFile file) {
        try {
            ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
            if (errorMap != null) return errorMap;
            noticeService.save(adminAddNoticeDto,file);
            return ResponseEntity.ok(new ControllerApiResponse(true,"작성 성공"));
            }catch (NoSuchElementException e){
                throw new NoticeException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "공지사항 삭제 API",description = "관리자만 문의사항 삭제가능",tags = {"공지사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"공지사항 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"작성자만 삭제할수 있습니다.\"}")))
    })
    @DeleteMapping("/api/admin/notices/{notice-id}")
    public ResponseEntity<?> deleteNotice(@PathVariable("notice-id") Long noticeId) {
        try{
            String loginId = authenticationLogin();
            noticeService.delete(loginId, noticeId);
            return ResponseEntity.ok(new ControllerApiResponse(true,"문의사항 삭제 성공"));
        } catch (NoSuchElementException e) {
            throw new NoticeNotFoundException(e.getMessage());
        }
    }

    @Operation(summary = "공지사항 수정 API", description = "관리자만 수정가능", tags = {"공지사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"noticeTitle\": \"[수정한 공지사항 제목]\", \"memberId\": \"[사용자 ID]\", \"noitceId\": \"[공지사항 ID]\", \"update_At\": \"[수정 시간]\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"관리자만 수정할수 있습니다.\"}")))
    })
    @PostMapping("/api/admin/notices/{notice-id}")
    public  ResponseEntity<?> updateNotice(@Valid @RequestPart AdminUpdateRequest updateNoticeDto, BindingResult bindingResult,
                                           @RequestPart(required = false) MultipartFile file, @PathVariable("notice-id") Long noticeId){
        try{
            ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
            if (errorMap != null) return errorMap;

            String loginId = authenticationLogin();
            noticeService.update(noticeId,updateNoticeDto,loginId,file);

            return ResponseEntity.ok(new ControllerApiResponse(true,"공지사항 수정 성공"));
        }catch (NoSuchElementException e){
            throw new BadRequestException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
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
