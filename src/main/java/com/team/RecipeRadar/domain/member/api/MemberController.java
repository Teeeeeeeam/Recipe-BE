package com.team.RecipeRadar.domain.member.api;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;
//    private final ApplicationEventPublisher eventPublisher;


    @PostMapping("/singup")
    public ResponseEntity<?> join(@Valid @RequestBody MemberDto memberDto , @RequestParam("code") String code, BindingResult result){
        try{
            boolean validationOfSignUp = memberService.ValidationOfSignUp(memberDto,code);
            if (!validationOfSignUp){
                ObjectError error = new ObjectError("globalError", "회원 가입시 모든 검사를 해주세요");
                result.addError(error);
            }

            if (result.hasErrors()) {
                Map<String,String> errorMessage=new HashMap<>();
                for (FieldError error : result.getFieldErrors()) {
                    errorMessage.put(error.getField(),error.getDefaultMessage());
                }
                ObjectError globalError = result.getGlobalError();
                errorMessage.put(globalError.getObjectName(), globalError.getDefaultMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            Member member = memberService.saveDto(memberDto);
//            eventPublisher.publishEvent(new MemberJoinEmailEvent(member));
            return ResponseEntity.ok(new ApiResponse(true,"회원가입 성공"));

        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류 발생");
        }

    }
}


