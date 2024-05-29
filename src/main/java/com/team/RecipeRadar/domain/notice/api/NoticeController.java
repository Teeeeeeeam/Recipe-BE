package com.team.RecipeRadar.domain.notice.api;

import com.team.RecipeRadar.domain.notice.application.NoticeService;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminUpdateRequest;
import com.team.RecipeRadar.domain.notice.dto.info.AdminInfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.info.AdminInfoNoticeResponse;
import com.team.RecipeRadar.domain.notice.exception.NoticeException;
import com.team.RecipeRadar.domain.notice.exception.ex.NoticeNotFoundException;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
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
@OpenAPIDefinition(tags = {
        @Tag(name = "관리자 공지사항 컨트롤러", description = "관리자 공지사항 작업")
})
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;
    private final S3UploadService s3UploadService;

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

            String uploadFile = s3UploadService.uploadFile(file);
            String originalFilename = file.getOriginalFilename();
            noticeService.save(adminAddNoticeDto,uploadFile,originalFilename);
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

    @Operation(summary = "메인 페이지 공지사항 API", description = "메인 페이지에서 보여질 공지사항 총 5개의 공지사항을 조회 등록한 최신순으로 조회", tags = {"공지사항 컨트롤러"})
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

    @Operation(summary = "어드민 공지사항 조회 API", description = "어드민 페이지에서 공자사항을 조회하는 페이징 API (무한페이징)", tags = {"공지사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"notice\":[{\"id\":1,\"noticeTitle\":\"첫 번째 공지사항\",\"created_at\":\"2024-05-28T17:08:00\",\"member\":{\"nickname\":\"관리자\"}},{\"id\":2,\"noticeTitle\":\"두 번째 공지사항\",\"created_at\":\"2024-05-28T13:00:00\",\"member\":{\"nickname\":\"관리자\"}},{\"id\":3,\"noticeTitle\":\"세 번째 공지사항\",\"created_at\":\"2024-05-28T13:00:00\",\"member\":{\"nickname\":\"관리자\"}}]}}\n"))),
    })
    @GetMapping("/api/admin/notices")
    public ResponseEntity<?> adminNotice(@RequestParam(value = "last-id",required = false)Long noticeId, Pageable pageable){
        AdminInfoNoticeResponse adminInfoNoticeResponse = noticeService.adminNotice(noticeId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",adminInfoNoticeResponse));
    }

    @Operation(summary = "어드민 공지사항 상세 조회 API", description = "어드민 페이지에서 noticeId의 대해서 상세 조회", tags = {"공지사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"id\":1,\"noticeTitle\":\"첫 번째 공지사항\",\"noticeContent\":\"첫 번째 공지사항 내용입니다.\",\"create_At\":\"2024-05-28T13:00:00\",\"member\":{\"id\":1,\"nickname\":\"관리자\"}}}"))),
    })
    @GetMapping("/api/admin/notice/{notice-id}")
    public ResponseEntity<?> adminDetailNotice(@PathVariable("notice-id") Long noticeId){
        AdminInfoDetailsResponse adminInfoDetailsResponse = noticeService.adminDetailNotice(noticeId);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",adminInfoDetailsResponse));
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
