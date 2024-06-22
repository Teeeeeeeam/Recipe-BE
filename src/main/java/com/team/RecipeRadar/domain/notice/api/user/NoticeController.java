package com.team.RecipeRadar.domain.notice.api.user;

import com.team.RecipeRadar.domain.notice.application.user.NoticeService;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.response.InfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.response.InfoNoticeResponse;
import com.team.RecipeRadar.global.payload.ErrorResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "공용 - 공지사항 컨트롤러",description = "공지사항 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "메인 공지사항", description = "메인 페이지에서 보여질 공지사항 총 5개의 공지사항을 조회 등록한 최신순으로 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":[{\"id\":3,\"noticeTitle\":\"첫 번째 공지사항\",\"imgUrl\":\"https://www.recipe.kr/6ae7cd95-f2f5-4112-8d2c-8da3c09538cc.PNG\"},{\"id\":11,\"noticeTitle\":\"두 번째 공지사항\",\"imgUrl\":\"https://www.recipe.kr/6ae7cd95-f2f5-4112-8d2c-8da3c09538cc.PNG\"}]}"))),
    })
    @GetMapping("/notice")
    public ResponseEntity<?> mainNotice(){
        List<NoticeDto> noticeDtoList = noticeService.mainNotice();
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",noticeDtoList));
    }

    @Operation(summary = "공지사항 조회(페이징)", description = "공자사항을 조회하는 무한페이징")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"notice\":[{\"id\":1,\"noticeTitle\":\"첫 번째 공지사항\",\"createdAt\":\"2024-05-28T17:08:00\",\"member\":{\"nickname\":\"관리자\"}},{\"id\":2,\"noticeTitle\":\"두 번째 공지사항\",\"createdAt\":\"2024-05-28T13:00:00\",\"member\":{\"nickname\":\"관리자\"}},{\"id\":3,\"noticeTitle\":\"세 번째 공지사항\",\"createdAt\":\"2024-05-28T13:00:00\",\"member\":{\"nickname\":\"관리자\"}}]}}\n"))),
    })
    @GetMapping("/notices")
    public ResponseEntity<?> adminNotice(@RequestParam(value = "lastId",required = false)Long noticeId,
                                         @Parameter(example = "{\"size\":10}") Pageable pageable){
        InfoNoticeResponse inInfoNoticeResponse = noticeService.noticeInfo(noticeId,pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",inInfoNoticeResponse));
    }

    @Operation(summary = "공지사항 상세 조회", description = "공지사항에 대한 상세 조회를 수행하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"id\":1,\"noticeTitle\":\"첫 번째 공지사항\",\"noticeContent\":\"첫 번째 공지사항 내용입니다.\",\"createdAt\":\"2024-05-28T13:00:00\",\"imgUrl\":\"https://recipe-reader-kr/ce250eb8-a62a-42be-8536-5fcf8498a63f.png\",\"member\":{\"id\":1,\"nickname\":\"관리자\"}}}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\":false , \"message\" : \"공지사항을 찾을수 없습니다\"}"))),
    })
    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<?> adminDetailNotice(@Schema(example = "1")@PathVariable("noticeId") Long noticeId){
        InfoDetailsResponse infoDetailsResponse = noticeService.detailNotice(noticeId);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",infoDetailsResponse));
    }

    @Operation(summary = "공지사항 검색", description = "공지사항에 제목을 통해 공지사항을 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"notice\":[{\"id\":1,\"noticeTitle\":\"첫 번째 공지사항\",\"createdAt\":\"2024-05-28T17:08:00\",\"member\":{\"nickname\":\"관리자\"}},{\"id\":2,\"noticeTitle\":\"두 번째 공지사항\",\"createdAt\":\"2024-05-28T13:00:00\",\"member\":{\"nickname\":\"관리자\"}},{\"id\":3,\"noticeTitle\":\"세 번째 공지사항\",\"createdAt\":\"2024-05-28T13:00:00\",\"member\":{\"nickname\":\"관리자\"}}]}}\n")))
    })
    @GetMapping("/notices/search")
    public ResponseEntity<?> searchNoticeTitle(@RequestParam(value = "title",required = false) String title,
                                               @RequestParam(value = "lastId",required = false) Long lastId,
                                               @Parameter(example = "{\"size\":10}")Pageable pageable){
        InfoNoticeResponse infoNoticeResponse = noticeService.searchNoticeWithTitle(title, lastId, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",infoNoticeResponse));
    }
}
