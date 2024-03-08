package com.team.RecipeRadar.controller;

import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.Service.JoinEmailService;
import com.team.RecipeRadar.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

/*
 회원가입후에 이메일인증 기능 버전 사용미정
 */
//@RestController
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationController {
    private final JoinEmailService emailService;
    private final MemberService memberService;

//    @GetMapping("/verify/email")
    public ResponseEntity<?> verifyEmail(@RequestParam String id){
        byte[] actualId = Base64.getDecoder().decode(id.getBytes());
        String username = emailService.getUsernameForVerificationId(new String(actualId));
        if(username!=null){
            Member user = memberService.findByLoginId(username);
            user.setVerified(true);
            memberService.saveEntity(user);
            return ResponseEntity.ok().body("redirect:/login-emailVerified");
        }
        return ResponseEntity.ok().body("redirect:/login-error");
    }
}
