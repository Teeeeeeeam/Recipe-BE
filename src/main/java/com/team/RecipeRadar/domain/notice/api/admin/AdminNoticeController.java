package com.team.RecipeRadar.domain.notice.api.admin;

import com.team.RecipeRadar.domain.notice.application.admin.AdminNoticeService;
import com.team.RecipeRadar.domain.notice.dto.request.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.request.AdminUpdateRequest;
import com.team.RecipeRadar.global.payload.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Tag(name = "어드민 - 공지사항 컨트롤러",description = "공지사항 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/admin")
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;

    @Operation(summary = "공지사항 작성", description = "관리자만 공지사항 작성 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"작성 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples =  @ExampleObject(value = "{\"success\": false, \"message\": \"관련 오류 내용\"}"))),
    })
    @PostMapping(value = "/notices", consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> noticeAdd(@Valid @RequestPart AdminAddRequest adminAddRequest, BindingResult bindingResult,
                                       @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @RequestPart(required = false) MultipartFile file) {
        ResponseEntity<ErrorResponse<Map<String, String>>> result = getErrorResponseResponseEntity(bindingResult);
        if (result != null) return result;

        adminNoticeService.save(adminAddRequest,principalDetails.getMemberId(),file);
        return ResponseEntity.ok(new ControllerApiResponse(true,"작성 성공"));
    }


    @Operation(summary = "공지사항 수정", description = "관리자만 수정가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"noticeTitle\": \"[수정한 공지사항 제목]\", \"memberId\": \"[사용자 ID]\", \"noitceId\": \"[공지사항 ID]\", \"update_At\": \"[수정 시간]\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false , \"message\" : \"관련 오류 내용\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"관리자만 수정할수 있습니다.\"}")))
    })
    @PutMapping(value = "/notices/{noticeId}",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<?> updateNotice(@Valid @RequestPart AdminUpdateRequest adminUpdateRequest, BindingResult bindingResult,
                                           @RequestPart(required = false) MultipartFile file, @PathVariable("noticeId") Long noticeId){
        ResponseEntity<ErrorResponse<Map<String, String>>> result = getErrorResponseResponseEntity(bindingResult);
        if (result != null) return result;
        adminNoticeService.update(noticeId,adminUpdateRequest,file);

        return ResponseEntity.ok(new ControllerApiResponse(true,"공지사항 수정 성공"));
    }

    private static ResponseEntity<ErrorResponse<Map<String, String>>> getErrorResponseResponseEntity(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> result = new LinkedHashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                result.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(new ErrorResponse<>(false, "실패", result));
        }
        return null;
    }

    @Operation(summary = "공지사항 삭제",description = "관리자만 문의사항 삭제가능 단일, 일괄 삭제가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"공지사항 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\": false, \"message\" : \"관련 오류 내용\"}")))
    })
    @DeleteMapping("/notices")
    public ResponseEntity<?> deleteNotice(@RequestParam("noticeIds") List<Long> noticeIds) {
        adminNoticeService.delete(noticeIds);
        return ResponseEntity.ok(new ControllerApiResponse(true,"공지사항 삭제 성공"));
    }
}
