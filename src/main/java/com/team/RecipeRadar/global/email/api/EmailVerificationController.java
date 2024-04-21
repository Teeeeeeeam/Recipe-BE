//package com.team.RecipeRadar.global.email.api;
//
//import com.team.RecipeRadar.domain.member.domain.Member;
//import com.team.RecipeRadar.global.email.application.JoinEmailService;
//import com.team.RecipeRadar.domain.member.application.MemberService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.Base64;
//
///*
// 회원가입후에 이메일인증 기능 버전 사용미정
// */
////@RestController
//@RequiredArgsConstructor
//@Slf4j
//public class EmailVerificationController {
//    private final JoinEmailService emailService;
//    private final MemberService memberService;
//
////    @GetMapping("/verify/email")
//    public ResponseEntity<?> verifyEmail(@RequestParam String id){
//        byte[] actualId = Base64.getDecoder().decode(id.getBytes());
//        String username = emailService.getUsernameForVerificationId(new String(actualId));
//        if(username!=null){
//            Member user = memberService.findByLoginId(username);
//            user.setVerified(true);
//            memberService.saveEntity(user);
//            return ResponseEntity.ok().body("redirect:/login-emailVerified");
//        }
//        return ResponseEntity.ok().body("redirect:/login-error");
//    }
//}
