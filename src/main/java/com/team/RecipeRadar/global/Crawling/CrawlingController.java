package com.team.RecipeRadar.global.Crawling;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private final CrawlingService crawlingService;

    @PostMapping("/crawling")
    public ResponseEntity<?> crawling(){
        crawlingService.save();
        return ResponseEntity.ok("크롤링중");
    }


    @PostMapping("/crawling_v1")
    public ResponseEntity<?> crawling_v1(){
        crawlingService.update_ingredient();
        return ResponseEntity.ok("크롤링 버전 1로 크로링중");
    }

    @PostMapping("/crawling_v2")
    public ResponseEntity<?> crawling_v2(){
        crawlingService.update_ingredient_v2();
        return ResponseEntity.ok("크롤링 버전 2로 크로링중");
    }

    @PostMapping("/crawling_v3")
    public ResponseEntity<?> crawling_v3(){
        crawlingService.update_ingredient_v3();
        return ResponseEntity.ok("크롤링 버전 3로 크로링중");
    }
}
