package com.team.RecipeRadar.domain.notice.api;

import com.team.RecipeRadar.domain.notice.application.NoticeService;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminUpdateRequest;
import com.team.RecipeRadar.domain.notice.dto.info.InfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.info.InfoNoticeResponse;
import com.team.RecipeRadar.domain.notice.exception.ex.NoticeNotFoundException;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@Tag(name = "어드민 - 공지사항 컨트롤러",description = "공지사항 관리")
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;
    private final S3UploadService s3UploadService;

    @Operation(summary = "공지사항 작성", description = "관리자만 공지사항 작성 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": \"작성 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples =  @ExampleObject(value = "{\"success\": false, \"message\": \"관련 오류 내용\"}"))),
    })
    @PostMapping(value = "/api/admin/notices", consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> noticeAdd(@Valid @RequestPart AdminAddRequest adminAddNoticeDto, BindingResult bindingResult,
                                       @RequestPart(required = false) MultipartFile file) {
        ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
        if (errorMap != null) return errorMap;
        String uploadFile=null;
        String originalFilename =null;
        if(file!=null){
            uploadFile = s3UploadService.uploadFile(file);
            originalFilename = file.getOriginalFilename();
        }
        noticeService.save(adminAddNoticeDto,uploadFile,originalFilename);
        return ResponseEntity.ok(new ControllerApiResponse(true,"작성 성공"));

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
    @DeleteMapping("/api/admin/notices")
    public ResponseEntity<?> deleteNotice(@RequestParam("ids") List<Long> noticeIds) {
        noticeService.delete(noticeIds);
        return ResponseEntity.ok(new ControllerApiResponse(true,"문의사항 삭제 성공"));
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
    @PutMapping(value = "/api/admin/notices/{notice-id}",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<?> updateNotice(@Valid @RequestPart AdminUpdateRequest adminUpdateRequest, BindingResult bindingResult,
                                           @RequestPart(required = false) MultipartFile file, @PathVariable("notice-id") Long noticeId){
        ResponseEntity<ErrorResponse<Map<String, String>>> errorMap = getErrorResponseResponseEntity(bindingResult);
        if (errorMap != null) return errorMap;
        noticeService.update(noticeId,adminUpdateRequest,file);

        return ResponseEntity.ok(new ControllerApiResponse(true,"공지사항 수정 성공"));
    }

    @Operation(summary = "메인 공지사항", description = "메인 페이지에서 보여질 공지사항 총 5개의 공지사항을 조회 등록한 최신순으로 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":[{\"id\":3,\"noticeTitle\":\"첫 번째 공지사항\",\"imgUrl\":\"https://www.recipe.kr/6ae7cd95-f2f5-4112-8d2c-8da3c09538cc.PNG\"},{\"id\":11,\"noticeTitle\":\"두 번째 공지사항\",\"imgUrl\":\"https://www.recipe.kr/6ae7cd95-f2f5-4112-8d2c-8da3c09538cc.PNG\"}]}"))),
    })
    @GetMapping("/api/notice")
    public ResponseEntity<?> mainNotice(){
        List<NoticeDto> noticeDtos = noticeService.mainNotice();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",noticeDtos));
    }

    @Operation(summary = "공지사항 조회(페이징)", description = "공자사항을 조회하는 무한페이징")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"notice\":[{\"id\":1,\"noticeTitle\":\"첫 번째 공지사항\",\"created_at\":\"2024-05-28T17:08:00\",\"member\":{\"nickname\":\"관리자\"}},{\"id\":2,\"noticeTitle\":\"두 번째 공지사항\",\"created_at\":\"2024-05-28T13:00:00\",\"member\":{\"nickname\":\"관리자\"}},{\"id\":3,\"noticeTitle\":\"세 번째 공지사항\",\"created_at\":\"2024-05-28T13:00:00\",\"member\":{\"nickname\":\"관리자\"}}]}}\n"))),
    })
    @GetMapping("/api/notices")
    public ResponseEntity<?> adminNotice(@RequestParam(value = "last-id",required = false)Long noticeId,
                                         @Parameter(example = "{\"size\":10}") Pageable pageable){
        InfoNoticeResponse inInfoNoticeResponse = noticeService.Notice(noticeId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",inInfoNoticeResponse));
    }

    @Operation(summary = "공지사항 상세 조회", description = "공지사항에 대한 상세 조회를 수행하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"id\":1,\"noticeTitle\":\"첫 번째 공지사항\",\"noticeContent\":\"첫 번째 공지사항 내용입니다.\",\"create_At\":\"2024-05-28T13:00:00\",\"img_url\":\"https://recipe-reader-kr/ce250eb8-a62a-42be-8536-5fcf8498a63f.png\",\"member\":{\"id\":1,\"nickname\":\"관리자\"}}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\"success\":false , \"message\" : \"공지사항을 찾을수 없습니다\"}"))),
    })
    @GetMapping("/api/notice/{notice-id}")
    public ResponseEntity<?> adminDetailNotice(@Schema(example = "1")@PathVariable("notice-id") Long noticeId){
        InfoDetailsResponse infoDetailsResponse = noticeService.detailNotice(noticeId);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",infoDetailsResponse));
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
