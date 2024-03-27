package com.team.RecipeRadar.domain.member.api;


import com.team.RecipeRadar.domain.member.application.AccountRetrievalService;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.email.application.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerErrorException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AccountRetrievalController {


    private final AccountRetrievalService accountRetrievalService;
    @Qualifier("AccountEmail")
    private final MailService mailService;

    /*
    아이디 찾기시 사용되는 엔드포인트
     */
    @GetMapping("/api/loginid/find")
    public ResponseEntity<?> test(@RequestBody MemberDto memberDto, @RequestParam("code")String code){
        try {
            String username = memberDto.getUsername();
            String email = memberDto.getEmail();

            List<Map<String, String>> loginId = accountRetrievalService.findLoginId(username, email, code);
            return ResponseEntity.ok(loginId);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("오류발생");
        }
    }
    
    /*
    아이디 찾기시 사용되는 이메일 엔드포인트
     */
    @PostMapping("/api/find/mailConfirm")
    public ResponseEntity<?> mailConfirm(@RequestParam("email") String email){
        try {
            Map<String,String> result= new LinkedHashMap<>();
            String code = mailService.sensMailMessage(email);

            result.put("인증번호",code);
            return ResponseEntity.ok(result);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }

    /*
    아이디 찾기시의 비밀번호 인증 검사
     */
    @PostMapping("/api/find/mailConfirm/check")
    public ResponseEntity<Map<String, Boolean>> check(@RequestParam("code")String UserCode){
        try {
            Map<String, Boolean> stringBooleanMap = mailService.verifyCode(UserCode);
            return ResponseEntity.ok(stringBooleanMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerErrorException("서버오류");
        }
    }
}
