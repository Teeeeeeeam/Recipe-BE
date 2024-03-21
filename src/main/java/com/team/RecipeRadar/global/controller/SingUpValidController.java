package com.team.RecipeRadar.global.controller;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.global.email.application.JoinEmailServiceImplV1;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SingUpValidController {

    private final MemberService memberService;
    private final JoinEmailServiceImplV1 joinEmailServiceImpl1;

    @PostMapping("/api/members/register/id/validate")
    public ResponseEntity<Map<String,Boolean>> LoginIdValid(@RequestBody MemberDto memberDto){
        try {
            Map<String, Boolean> stringBooleanMap = memberService.LoginIdValid(memberDto);
            return ResponseEntity.ok(stringBooleanMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }

    }

    @PostMapping("/api/members/checkPasswordDuplication")
    public ResponseEntity<Map<String,Boolean>> duplicatePassword(@RequestBody MemberDto memberDto){
        try{
            Map<String, Boolean> stringBooleanMap = memberService.duplicatePassword(memberDto);
            return ResponseEntity.ok(stringBooleanMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }

    }

    @PostMapping("/api/members/checkPasswordStrength")
    public ResponseEntity<Map<String,Boolean>> checkPasswordStrength(@RequestBody MemberDto memberDto){
        try {
            Map<String, Boolean> stringBooleanMap = memberService.checkPasswordStrength(memberDto);
            return ResponseEntity.ok(stringBooleanMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");

        }
    }

    @PostMapping("/api/members/checkNickNameValidity")
    public ResponseEntity<Map<String,Boolean>> nickNameValid(@RequestBody MemberDto memberDto){
        try {
            Map<String, Boolean> stringBooleanMap = memberService.nickNameValid(memberDto);
            return ResponseEntity.ok(stringBooleanMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @PostMapping("/api/members/checkUserNameValidity")
    public ResponseEntity<Map<String,Boolean>> userNameValid(@RequestBody MemberDto memberDto){
        try {
            Map<String, Boolean> stringBooleanMap = memberService.userNameValid(memberDto);
            return ResponseEntity.ok(stringBooleanMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @PostMapping("/api/members/checkEmailValidity")
    public ResponseEntity<Map<String,Boolean>> emailValid(@RequestBody MemberDto memberDto){
        try {

            Map<String, Boolean> stringBooleanMap = memberService.emailValid(memberDto);
            return ResponseEntity.ok(stringBooleanMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @PostMapping("/api/members/validateSignUp")
    public ResponseEntity<Map<String,Boolean>> ValidationOfSignUp(@RequestBody MemberDto memberDto,String code){
        try {
            Map<String, Boolean> map = new LinkedHashMap<>();
            boolean valid = memberService.ValidationOfSignUp(memberDto,code);
            map.put("isValidateSignUp", valid);
            return ResponseEntity.ok(map);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @PostMapping("/api/join/mailConfirm")
    public ResponseEntity<?> mailConfirm(@RequestParam("email") String email){
        try {
            Map<String,String> result= new LinkedHashMap<>();
            String code = joinEmailServiceImpl1.sensMailMessage(email);

            result.put("인증번호",code);
            return ResponseEntity.ok(result);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    @PostMapping("/api/join/mailConfirm/check")
    public ResponseEntity<Map<String, Boolean>> check(@RequestParam("code")String UserCode){
        try {
            Map<String, Boolean> stringBooleanMap = memberService.verifyCode(UserCode);
            return ResponseEntity.ok(stringBooleanMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }
}
