package com.team.RecipeRadar.controller;

import com.team.RecipeRadar.Entity.Inquiry;
import com.team.RecipeRadar.dto.AddInquiryRequest;
import com.team.RecipeRadar.dto.InquiryResponse;
import com.team.RecipeRadar.dto.UpdateInquiryRequest;
import com.team.RecipeRadar.Service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping("/api/inquires")
    public ResponseEntity<Inquiry> addInquiry(@RequestBody AddInquiryRequest request) {
        Inquiry savedInquiry = inquiryService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedInquiry);
    }

    @GetMapping("/api/admin/inquires")
    public  ResponseEntity<List<InquiryResponse>> findAllInquires() {
        List<InquiryResponse> inquires = inquiryService.findAll()
                .stream()
                .map(InquiryResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(inquires);
    }
    @GetMapping("api/admin/inquires/{id}")
    public  ResponseEntity<InquiryResponse> findInquiry(@PathVariable long id) {
        Inquiry inquiry = inquiryService.findById(id);

        return  ResponseEntity.ok()
                .body(new InquiryResponse(inquiry));
    }

    @DeleteMapping("/api/inquires/{id}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable long id) {
        inquiryService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/admin/inquires/{id}")
    public  ResponseEntity<Inquiry> updateInquiry(@PathVariable long id, @RequestBody UpdateInquiryRequest request){
        Inquiry updateInquiry = inquiryService.update(id, request);

        return ResponseEntity.ok()
                .body(updateInquiry);
    }

    @PostMapping("/api/admin/inquires/{id}/answer")
    public ResponseEntity<Inquiry> addInquiryAnswer(@PathVariable long id, @RequestBody AddInquiryRequest request) {
        Inquiry savedInquiryAnswer = inquiryService.saveAnswer(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedInquiryAnswer);
    }
    @PostMapping("/api/admin/inquires/{id}/answered")
    public ResponseEntity<Inquiry> inquiryAnswered(@PathVariable long id, @RequestBody AddInquiryRequest request) {
        Inquiry savedInquiryAnswered = inquiryService.saveAnswered(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedInquiryAnswered);
    }
}
