package com.team.RecipeRadar.domain.notice.api;

import com.team.RecipeRadar.domain.inquiry.exception.ex.InquiryNotFoundException;
import com.team.RecipeRadar.domain.notice.application.NoticeService;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.NoticeResponse;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddNoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminDeleteNoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminUpdateNoticeDto;
import com.team.RecipeRadar.domain.notice.exception.NoticeException;
import com.team.RecipeRadar.domain.notice.exception.ex.NoticeNotFoundException;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@OpenAPIDefinition(tags = {
        @Tag(name = "공지사항 컨트롤러", description = "관리자 공지사항 작업")
})
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 작성 API", description = "관리자만 공지사항 작성 가능", tags = {"공지사항 컨트롤러"} )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\": {\"NoticeTitle\": \"[작성한 공지사항제목]\", \"memberId\": \"[사용자 ID]\", \"inquiryId\": \"[문의사항 ID]\", \"created_at\": \"LocalDateTime\"}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/api/admin/notices")
    public ResponseEntity<?> addNotice(@Valid @RequestBody AdminAddNoticeDto adminAddNoticeDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()){
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
            }
            Notice save = noticeService.save(adminAddNoticeDto);

            AdminAddNoticeDto addResponse = new AdminAddNoticeDto(
                    save.getNoticeTitle(),
                    save.getMember().getId());

            return ResponseEntity.ok(new ControllerApiResponse(true,"성공", addResponse));
        }catch (NoSuchElementException e){
            throw new NoticeException(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @Operation(summary = "전체 공지사항 조회 API", description = "관리자만 전체를 조회할 수 있습니다.", tags = {"공지사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NoticeResponse.class)),
                            examples = @ExampleObject(value = "[{\"noticeId\": \"[공지사항 ID]\", \"noticeTitle\": \"[공지사항 제목]\", \"memberId\": \"[사용자 ID]\", \"created_at\": \"LocalDateTime\"}]"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/api/user/notices")
    public  ResponseEntity<List<NoticeResponse>> findAllNotices() {
        List<NoticeResponse> notices = noticeService.findAll()
                .stream()
                .map(NoticeResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(notices);
    }

    @Operation(summary = "공지사항 상세 조회 API", description = "사용자가 공지사항의 상세 정보를 조회할 수 있습니다.", tags = {"공지사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = NoticeResponse.class),
                            examples = @ExampleObject(value = "{\"noticeId\": \"[공지사항 ID]\", \"noticeTitle\": \"[공지사항 제목]\", \"memberId\": \"[사용자 ID]\", \"created_at\": \"LocalDateTime\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("api/user/notices/{id}")
    public  ResponseEntity<NoticeResponse> findNotice(@PathVariable long id) {
        Notice notice = noticeService.findById(id);

        return  ResponseEntity.ok()
                .body(new NoticeResponse(notice));
    }

    @Operation(summary = "공지사항 삭제 API",description = "관리자만 문의사항 삭제가능",tags = {"공지사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\": true, \"message\" : \"공지사항 삭제 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

    })
    @DeleteMapping("/api/admin/notices/{id}")
    public ResponseEntity<?> deleteNotice(@RequestBody AdminDeleteNoticeDto adminDeleteNoticeDto) {
        try{
            noticeService.delete(adminDeleteNoticeDto);
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
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/api/admin/notices/{id}")
    public  ResponseEntity<?> updateNotice(@Valid @RequestBody AdminUpdateNoticeDto updateNoticeDto, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, bindingResult.getFieldError().getDefaultMessage()));
            }

            noticeService.update(
                    updateNoticeDto.getMemberId(),
                    updateNoticeDto.getNoticeId(),
                    updateNoticeDto.getNoticeTitle()
            );
            Notice notice = noticeService.findById(updateNoticeDto.getNoticeId());
            AdminUpdateNoticeDto adminUpdateNoticeDto = new AdminUpdateNoticeDto(
                    notice.getNoticeTitle(),
                    notice.getMember().getId(),
                    notice.getId()
            );
            return ResponseEntity.ok(new ControllerApiResponse(true,"문의사항 수정 성공", adminUpdateNoticeDto));
        }catch (NoSuchElementException e){
            throw new InquiryNotFoundException(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버 오류 발생");
        }
    }
}
