package com.team.RecipeRadar.domain.questions.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.application.QuestionService;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@Tag(name = "문의사항 컨트롤러",description = "문의사항의 관한 컨트롤러")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    //사용자 계정 관련 질문 등록할때
    @Operation(summary = "사용자 계정 비활성화 문의",description = "추방당한 사용자가 해당 문의사항을 사용가능하다. 해당 문의사항 작성시 현재 로그인한 어드민에게 실시간으로 알림이 가도록 설정")
    @PostMapping(value = "/api/question",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> accountQuestion(@RequestPart QuestionRequest questionRequest, @RequestPart(required = false) MultipartFile file){
        questionService.account_Question(questionRequest,file);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"비로그인 문의 사항 등록"));
    }

    //로그인한 사용자들의 일반 문의
    @Operation(summary = "사용자 일반 문의",description = "로그인한 사용자에 대해서만 문의사항 작성 가능 해당 문의사항 작성시 현재 로그인한 어드민에게 실시간으로 알림이 가도록 설정")
    @PostMapping(value = "/api/user/question",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generalQuestion(@RequestPart QuestionRequest questionRequest, @RequestPart(required = false) MultipartFile file){
        questionService.general_Question(questionRequest,file);
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"문의 사항 등록"));
    }


    @Operation(summary = "어드민 사용자 문의사항 상세조회",description = "문의사항의 대해서 상세 조회된다.")
    @GetMapping("/api/admin/question/{id}")
    public ResponseEntity<?> details_Question(@PathVariable("id") Long questionId,
                                              @Parameter(hidden = true)@AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        QuestionDto questionDto = questionService.detailAdmin_Question(questionId, memberDto.getLoginId());
        return ResponseEntity.ok(new ControllerApiResponse<>(true,"조회 성공",questionDto));
    }
}
