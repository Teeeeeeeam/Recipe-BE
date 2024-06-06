package com.team.RecipeRadar.domain.questions.api;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.questions.application.QuestionService;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import com.team.RecipeRadar.global.security.basic.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    //사용자 계정 관련 질문 등록할때
    @PostMapping(value = "/api/question",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> accountQuestion(@RequestPart QuestionRequest questionRequest, @RequestPart(required = false) MultipartFile file){
        questionService.account_Question(questionRequest,file);
        return ResponseEntity.ok("저장성공");
    }

    //로그인한 사용자들의 일반 문의
    @PostMapping(value = "/api/user/question",consumes= MediaType.MULTIPART_FORM_DATA_VALUE ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generalQuestion(@RequestPart QuestionRequest questionRequest, @RequestPart(required = false) MultipartFile file){
        questionService.general_Question(questionRequest,file);
        return ResponseEntity.ok("저장성공");
    }

    //어드민의 상세조회
    @GetMapping("/api/admin/question/{id}")
    public ResponseEntity<?> details_Question(@PathVariable("id") Long questionId, @AuthenticationPrincipal PrincipalDetails principalDetails){
        MemberDto memberDto = principalDetails.getMemberDto(principalDetails.getMember());
        QuestionDto questionDto = questionService.detailAdmin_Question(questionId, memberDto.getLoginId());
        return ResponseEntity.ok(questionDto);
    }
}
