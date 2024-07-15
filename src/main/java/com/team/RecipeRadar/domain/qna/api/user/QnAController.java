package com.team.RecipeRadar.domain.qna.api.user;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.qna.application.user.QnAService;
import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import com.team.RecipeRadar.domain.qna.dto.response.QuestionAllResponse;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QnAController {

    private final QnAService qnAService;

    //사용자 계정 관련 질문 등록할때
    @Tag(name = "사용자 - 문의사항 컨트롤러",description = "문의사항 조회 및 삭제")
    @Operation(summary = "사용자 계정 비활성화 문의",description = "추방당한 사용자가 해당 문의사항을 사용가능하다. 해당 문의사항 작성시 현재 로그인한 어드민에게 실시간으로 알림이 가도록 설정",tags = "사용자 - 문의사항 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"비로그인 문의 사항 등록\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples =  @ExampleObject(value = "{\"success\":false,\"message\":\"실패\",\"data\":{\"필드명\" : \"필드 오류 내용\"}}"))),
    })
    @PostMapping(value = "/question",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> accountQuestion(@Valid @RequestPart QuestionRequest questionRequest, BindingResult bindingResult,
                                             @RequestPart(required = false) MultipartFile file){
        qnAService.accountQuestion(questionRequest,file);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"비로그인 문의 사항 등록"));
    }

    //로그인한 사용자들의 일반 문의
    @Operation(summary = "사용자 일반 문의",description = "로그인한 사용자에 대해서만 문의사항 작성 가능 해당 문의사항 작성시 현재 로그인한 어드민에게 실시간으로 알림이 가도록 설정",tags = "사용자 - 문의사항 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"문의 사항 등록\"}"))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples =  @ExampleObject(value = "{\"success\":false,\"message\":\"실패\",\"data\":{\"필드명\" : \"필드 오류 내용\"}}"))),
    })
    @PostMapping(value = "/user/question",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generalQuestion(@Valid @RequestPart QuestionRequest questionRequest, BindingResult result,
                                             @RequestPart(required = false) MultipartFile file,
                                             @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        qnAService.generalQuestion(questionRequest,principalDetails.getMemberId(),file);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"문의 사항 등록"));
    }


    @Operation(summary = "문의사항 전체 조회",description = "문의사항의 대해서 상세 조회된다.\n question-type={ACCOUNT_INQUIRY[계정 문의],GENERAL_INQUIRY[일반 문의]} ,question_status={PENDING[대기중],COMPLETED[완료]}",tags = "사용자 - 마이페이지 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"nextPage\":true,\"questions\":[{\"id\":1,\"questionType\":\"GENERAL_INQUIRY\",\"title\":\"문의 사항 제목\",\"status\":\"PENDING\",\"createdAt\":\"2024-06-10\",\"member\":{\"id\":1,\"loginId\":\"user1234\"}}]}}")))
    })
    @GetMapping("/user/questions")
    public ResponseEntity<?> question_user_all(@RequestParam(name = "lastId",required = false)Long lastId,
                                          @RequestParam(name = "questionType",required = false) QuestionType questionType,
                                          @RequestParam(name = "questionStatus",required = false) QuestionStatus questionStatus,
                                          @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails,
                                          @Parameter(example = "{\"size\":10}") Pageable pageable){
        QuestionAllResponse questionAllResponse = qnAService.allUserQuestion(lastId,principalDetails.getMemberId(), questionType, questionStatus, pageable);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",questionAllResponse));
    }
    @Operation(summary = "문의사항 삭제",description = "사용자는 작성한 문의사항의 대해서 삭제가능하며 단일,일괄 삭제가능",tags = {"사용자 - 마이페이지 컨트롤러","사용자 - 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"삭제 성공\"}"))),
            @ApiResponse(responseCode = "403",description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"작성자만 삭제 가능합니다.\"}")))
    })
    @DeleteMapping("/user/questions")
    public ResponseEntity<?> question_delete(@RequestParam("questionIds") List<Long> questionIds,
                                             @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        qnAService.deleteQuestions(questionIds,principalDetails.getMemberId());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"삭제 성공"));
    }

    @Operation(summary = "문의사항 상세 조회",description = "작성했던 문의사항의 대해서 조회하는 API 로그인한 사용자만 열럼가능 하며 작성자만 열럼가능하다.",tags = {"사용자 - 마이페이지 컨트롤러","사용자 - 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value ="{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"id\":9,\"title\":\"문의 사항 제목\",\"questionContent\":\"문의 사항 내용\",\"status\":\"COMPLETED\",\"createAt\":\"2024-06-10\",\"answerAt\":\"2024-06-11\",\"imgUrl\" :\"https://www.recipe.o-r.kr/aad8ae64-d30f-4b73-99e0-09c50b7e9379.png\",\"member\":{\"id\":1,\"loginId\":\"user1234\"},\"answer\":{\"id\":3,\"answerTitle\":\"답변 제목\",\"answerContent\":\"답변 내용\",\"answerAdminNickname\":\"어드민사용자\"}}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"존재하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "403",description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"사용자를 찾을 수 없습니다.\"}")))
    })
    @GetMapping("/user/question/{questionId}")
    public ResponseEntity<?> test(@PathVariable("questionId") Long questionId,
                                  @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){

        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        QuestionDto questionDto = qnAService.viewResponse(memberDto,questionId);

        return ResponseEntity.ok(new ControllerApiResponse<>(true, "조회 성공",questionDto));
    }
}
