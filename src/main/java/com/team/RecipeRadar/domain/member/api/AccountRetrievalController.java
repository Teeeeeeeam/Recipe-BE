package com.team.RecipeRadar.domain.member.api;


import com.team.RecipeRadar.domain.member.application.AccountRetrievalService;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AccountRetrievalController {


    private final AccountRetrievalService accountRetrievalService;

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
}
