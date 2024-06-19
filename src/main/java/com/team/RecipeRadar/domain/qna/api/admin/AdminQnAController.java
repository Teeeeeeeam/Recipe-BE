package com.team.RecipeRadar.domain.qna.api.admin;

import com.team.RecipeRadar.domain.qna.application.admin.AdminQnAService;
import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import com.team.RecipeRadar.domain.qna.dto.response.QuestionAllResponse;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@Tag(name = "어드민 - 문의사항 컨트롤러",description = "문의사항 관리 및 답변")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminQnAController {

    private final AdminQnAService adminQnAService;


    @Operation(summary = "문의사항 상세조회",description = "문의사항의 대해서 상세 조회된다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"questionType\":\"GENERAL_INQUIRY\",\"title\":\"문의사항 질문 \",\"questionContent\":\"질문 내용\",\"status\":\"COMPLETED\",\"answerType\":\"EMAIL\",\"createdAt\":\"2024-06-10\",\"imgUrl\" :\"https://www.recipe.o-r.kr/aad8ae64-d30f-4b73-99e0-09c50b7e9379.png\",\"answerEmail\":\"keuye06380618@naver.com\",\"member\":{\"id\":1,\"nickname\":\"일반사용자\",\"loginId\":\"user1234\"}}}"))),
            @ApiResponse(responseCode = "403",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"관리자만 접근 가능 가능합니다.\"}")))
    })
    @GetMapping("/question/{questionId}")
    public ResponseEntity<?> detailsQuestion(@PathVariable("questionId") Long questionId,
                                             @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails){
        QuestionDto questionDto = adminQnAService.detailAdminQuestion(questionId, principalDetails.getMemberId());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",questionDto));
    }

    @Operation(summary = "문의사항 전체 조회",description = "문의사항의 대해서 상세 조회된다.\n questionType={ACCOUNT_INQUIRY[계정 문의],GENERAL_INQUIRY[일반 문의]} ,questionStatus={PENDING[대기중],COMPLETED[완료]} ",tags = "어드민 - 문의사항 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"questions\":[{\"id\":1,\"questionType\":\"GENERAL_INQUIRY\",\"title\":\"문의 사항 제목\",\"status\":\"PENDING\",\"createdAt\":\"2024-06-10\",\"member\":{\"id\":1,\"loginId\":\"user1234\"}}]}}")))
    })
    @GetMapping("/questions")
    public ResponseEntity<?> questionAll(@RequestParam(name = "lastId",required = false)Long lastId,
                                         @RequestParam(name = "questionType",required = false) QuestionType questionType,
                                         @RequestParam(name = "questionStatus",required = false) QuestionStatus questionStatus,
                                         @Parameter(example = "{\"size\":10}") Pageable pageable){

        QuestionAllResponse questionAllResponse = adminQnAService.allQuestion(lastId, questionType, questionStatus, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",questionAllResponse));
    }

    @Operation(summary = "문의사항 답변",description = "문의 사항에 대해서 관리자는 답변을 남긴다. 문의사항 작성시 이메일 수신의 동의한 유저는 답변 등록시 이메일로 답변 완료 이메일 전송, 계정 관련 답변은 해당 작성한 입력한 이메일로 답변 전송",tags = "어드민 - 문의사항 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"답변 작성 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"[오류 내용]\"}")))
    })
    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<?> answer(@PathVariable Long questionId,
                                    @Valid @RequestBody QuestionAnswerRequest questionAnswerRequest, BindingResult bindingResult,
                                    @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails){

        ResponseEntity<ErrorResponse<List<String>>> result = getErrorResponseResponseEntity(bindingResult);
        if (result != null) return result;

        adminQnAService.questionAnswer(questionId, questionAnswerRequest, principalDetails.getNickName());

        return ResponseEntity.ok(new ControllerApiResponse<>(true, "답변 작성 성공"));
    }

    private static ResponseEntity<ErrorResponse<List<String>>> getErrorResponseResponseEntity(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> result = new LinkedList<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                result.add( error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(new ErrorResponse<>(false, "실패", result));
        }
        return null;
    }

}
