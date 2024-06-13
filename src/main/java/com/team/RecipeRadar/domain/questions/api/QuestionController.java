package com.team.RecipeRadar.domain.questions.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.application.QuestionService;
import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import com.team.RecipeRadar.domain.questions.dto.QuestionAllResponse;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import com.team.RecipeRadar.global.exception.ErrorResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;


    //사용자 계정 관련 질문 등록할때
    @Operation(summary = "사용자 계정 비활성화 문의",description = "추방당한 사용자가 해당 문의사항을 사용가능하다. 해당 문의사항 작성시 현재 로그인한 어드민에게 실시간으로 알림이 가도록 설정",tags = "사용자 - 문의사항 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"비로그인 문의 사항 등록\"}")))
    })
    @PostMapping(value = "/api/question",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> accountQuestion(@RequestPart QuestionRequest questionRequest, @RequestPart(required = false) MultipartFile file){
        questionService.account_Question(questionRequest,file);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"비로그인 문의 사항 등록"));
    }

    //로그인한 사용자들의 일반 문의
    @Operation(summary = "사용자 일반 문의",description = "로그인한 사용자에 대해서만 문의사항 작성 가능 해당 문의사항 작성시 현재 로그인한 어드민에게 실시간으로 알림이 가도록 설정",tags = "사용자 - 문의사항 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"문의 사항 등록\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"문의사항이 존재하지 않습니다.\"}")))
    })
    @PostMapping(value = "/api/user/question",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generalQuestion(@RequestPart QuestionRequest questionRequest,
                                             @RequestPart(required = false) MultipartFile file,
                                             @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberDto memberDto = getMemberDto(principalDetails);
        questionService.general_Question(questionRequest,memberDto.getId(),file);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"문의 사항 등록"));
    }


    @Tag(name = "어드민 - 문의사항 컨트롤러",description = "문의사항 관리 및 답변")
    @Operation(summary = "문의사항 상세조회",description = "문의사항의 대해서 상세 조회된다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
            content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"questionType\":\"GENERAL_INQUIRY\",\"title\":\"문의사항 질문 \",\"question_content\":\"질문 내용\",\"status\":\"COMPLETED\",\"answerType\":\"EMAIL\",\"create_at\":\"2024-06-10T08:03:37.126042\",\"img_url\" :\"https://www.recipe.o-r.kr/aad8ae64-d30f-4b73-99e0-09c50b7e9379.png\",\"answer_email\":\"keuye06380618@naver.com\",\"member\":{\"id\":1,\"nickname\":\"일반사용자\",\"loginId\":\"user1234\"}}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"관리자만 접근 가능 가능합니다.\"}")))
    })
    @GetMapping("/api/admin/question/{id}")
    public ResponseEntity<?> details_Question(@PathVariable("id") Long questionId,
                                              @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberDto memberDto = getMemberDto(principalDetails);
        QuestionDto questionDto = questionService.detailAdmin_Question(questionId, memberDto.getLoginId());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",questionDto));
    }

    @Operation(summary = "문의사항 전체 조회",description = "문의사항의 대해서 상세 조회된다.\n question-type={ACCOUNT_INQUIRY[계정 문의],GENERAL_INQUIRY[일반 문의]} ,question_status={PENDING[대기중],COMPLETED[완료]} ",tags = "어드민 - 문의사항 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"questions\":[{\"id\":1,\"questionType\":\"GENERAL_INQUIRY\",\"title\":\"문의 사항 제목\",\"status\":\"PENDING\",\"create_at\":\"2024-06-10T16:41:01.300294\",\"member\":{\"id\":1,\"loginId\":\"user1234\"}}]}}")))
    })
    @GetMapping("/api/admin/questions")
    public ResponseEntity<?> question_all(@RequestParam(name = "last-id",required = false)Long lastId,
                                          @RequestParam(name = "question-type",required = false) QuestionType questionType,
                                          @RequestParam(name = "question_status",required = false) QuestionStatus questionStatus,
                                          @Parameter(example = "{\"size\":10}") Pageable pageable){

        QuestionAllResponse questionAllResponse = questionService.allQuestion(lastId, questionType, questionStatus, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",questionAllResponse));
    }

    @Tag(name = "사용자 - 문의사항 컨트롤러",description = "문의사항 조회 및 삭제")
    @Operation(summary = "문의사항 전체 조회",description = "문의사항의 대해서 상세 조회된다.\n question-type={ACCOUNT_INQUIRY[계정 문의],GENERAL_INQUIRY[일반 문의]} ,question_status={PENDING[대기중],COMPLETED[완료]}",tags = "사용자 - 마이페이지 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"questions\":[{\"id\":1,\"questionType\":\"GENERAL_INQUIRY\",\"title\":\"문의 사항 제목\",\"status\":\"PENDING\",\"create_at\":\"2024-06-10T16:41:01.300294\",\"member\":{\"id\":1,\"loginId\":\"user1234\"}}]}}")))
    })
    @GetMapping("/api/user/questions")
    public ResponseEntity<?> question_user_all(@RequestParam(name = "last-id",required = false)Long lastId,
                                          @RequestParam(name = "question-type",required = false) QuestionType questionType,
                                          @RequestParam(name = "question_status",required = false) QuestionStatus questionStatus,
                                          @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails,
                                          @Parameter(example = "{\"size\":10}") Pageable pageable){
        MemberDto memberDto = getMemberDto(principalDetails);
        QuestionAllResponse questionAllResponse = questionService.allUserQuestion(lastId,memberDto.getId(), questionType, questionStatus, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",questionAllResponse));
    }
    @Operation(summary = "문의사항 삭제",description = "사용자는 작성한 문의사항의 대해서 삭제가능하며 단일,일괄 삭제가능",tags = {"사용자 - 마이페이지 컨트롤러","사용자 - 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"삭제 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"작성자만 삭제 가능합니다.\"}")))
    })
    @DeleteMapping("/api/user/questions")
    public ResponseEntity<?> question_delete(@RequestParam("ids") List<Long> ids,
                                             @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberDto memberDto = getMemberDto(principalDetails);
        questionService.deleteQuestions(ids,memberDto);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"삭제 성공"));
    }

    private static MemberDto getMemberDto(PrincipalDetails principalDetails) {
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        return memberDto;
    }
}
