package com.team.RecipeRadar.domain.questions.api;


import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.application.AnswerService;
import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    @Tag(name = "어드민 - 문의사항 컨트롤러",description = "문의사항 관리 및 답변")
    @Operation(summary = "문의사항 답변",description = "문의 사항에 대해서 관리자는 답변을 남긴다. 문의사항 작성시 이메일 수신의 동의한 유저는 답변 등록시 이메일로 답변 완료 이메일 전송, 계정 관련 답변은 해당 작성한 입력한 이메일로 답변 전송",tags = "어드민 - 문의사항 컨트롤러")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value = "{\"success\":true,\"message\":\"답변 작성 성공\"}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"문의사항이 존재하지 않습니다.\"}")))
    })
    @PostMapping("/api/admin/questions/{questionId}/answers")
    public ResponseEntity<?> answer(@PathVariable Long questionId, @RequestBody QuestionAnswerRequest questionAnswerRequest,
                                    @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails){
        try {
            MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
            answerService.questionAnswer(questionId, questionAnswerRequest, memberDto.getNickname());

            return ResponseEntity.ok(new ControllerApiResponse<>(true, "답변 작성 성공"));
        }catch (Exception e){
            e.printStackTrace();
            throw  new ServerErrorException(e.getMessage());
        }
    }

    @Tag(name = "사용자 - 문의사항 컨트롤러",description = "문의사항 조회 및 삭제")
    @Operation(summary = "문의사항 상세 조회",description = "작성했던 문의사항의 대해서 조회하는 API 로그인한 사용자만 열럼가능 하며 작성자만 열럼가능하다.",tags = {"사용자 - 마이페이지 컨트롤러","사용자 - 문의사항 컨트롤러"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ControllerApiResponse.class),
                            examples = @ExampleObject(value ="{\"success\":true,\"message\":\"조회 성공\",\"data\":{\"id\":9,\"title\":\"문의 사항 제목\",\"question_content\":\"문의 사항 내용\",\"status\":\"COMPLETED\",\"create_at\":\"2024-06-10T16:37:00\",\"answer_at\":\"2024-06-11T16:37:00\",\"img_url\" :\"https://www.recipe.o-r.kr/aad8ae64-d30f-4b73-99e0-09c50b7e9379.png\",\"member\":{\"id\":1,\"loginId\":\"user1234\"},\"answer\":{\"id\":3,\"answer_title\":\"답변 제목\",\"answer_content\":\"답변 내용\",\"answer_admin_nickname\":\"어드민사용자\"}}}"))),
            @ApiResponse(responseCode = "400",description = "BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"존재하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "403",description = "FORBIDDEN",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"success\" : false, \"message\" : \"사용자를 찾을 수 없습니다.\"}")))
    })
    @GetMapping("/api/user/question/{questionId}")
    public ResponseEntity<?> test(@PathVariable("questionId") Long questionId,
                                  @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){

        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        QuestionDto questionDto = answerService.viewResponse(memberDto,questionId);

        return ResponseEntity.ok(new ControllerApiResponse<>(true, "조회 성공",questionDto));
    }
}
