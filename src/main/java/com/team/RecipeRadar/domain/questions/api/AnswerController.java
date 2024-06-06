package com.team.RecipeRadar.domain.questions.api;


import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.application.AnswerService;
import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    @Operation(summary = "문의사항 답변",description = "문의 사항에 대해서 관리자는 답변을 남긴다. 문의사항 작성시 이메일 수신의 동의한 유저는 답변 등록시 이메일로 답변 완료 이메일 전송, 계정 관련 답변은 해당 작성한 입력한 이메일로 답변 전송")
    @PostMapping("/api/admin/questions/{questionId}/answers")
    public ResponseEntity<?> answer(@PathVariable Long questionId, @RequestBody QuestionAnswerRequest questionAnswerRequest,
                                   @Schema(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails){
        try {
            MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
            answerService.question_answer(questionId, questionAnswerRequest, memberDto.getNickname());

            return ResponseEntity.ok(new ControllerApiResponse<>(true, "답변 작성 성공"));
        }catch (Exception e){
            e.printStackTrace();
            throw  new ServerErrorException(e.getMessage());
        }
    }
}
