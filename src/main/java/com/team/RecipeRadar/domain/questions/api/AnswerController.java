package com.team.RecipeRadar.domain.questions.api;


import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.application.AnswerService;
import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @PostMapping("/api/admin/questions/{questionId}/answers")
    public ResponseEntity<?> answer(@PathVariable Long questionId, @RequestBody QuestionAnswerRequest questionAnswerRequest,
                                    @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails){
        try {
            MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
            answerService.question_answer(questionId, questionAnswerRequest, memberDto.getNickname());

            return ResponseEntity.ok(new ControllerApiResponse<>(true, "답변 작성 성공"));
        }catch (Exception e){
            e.printStackTrace();
            throw  new ServerErrorException(e.getMessage());
        }
    }

    @Tag(name = "사용자 - 문의사항 컨트롤러",description = "문의사항 조회 및 삭제")
    @Operation(summary = "문의사항 상세 조회",description = "작성했던 문의사항의 대해서 조회하는 API 로그인한 사용자만 열럼가능 하며 작성자만 열럼가능하다.",tags = {"사용자 - 마이페이지 컨트롤러","사용자 - 문의사항 컨트롤러"})
    @GetMapping("/api/user/question/{questionId}")
    public ResponseEntity<?> test(@PathVariable("questionId") Long questionId,
                                  @Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){

        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        QuestionDto questionDto = answerService.viewResponse(memberDto,questionId);

        return ResponseEntity.ok(new ControllerApiResponse<>(true, "조회 성공",questionDto));
    }
}
