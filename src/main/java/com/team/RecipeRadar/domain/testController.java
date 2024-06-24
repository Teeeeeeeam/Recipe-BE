package com.team.RecipeRadar.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class testController {

    @GetMapping("/api/admin/test")
    public String admin(){
        log.info("tlasd");
        return "일반사용자는 접근 불가애야하는데..?";
    }
}
