package com.team.RecipeRadar.domain.questions.api;


import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.application.AnswerService;
import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
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
