package com.team.RecipeRadar.controller;

import com.team.RecipeRadar.Entity.Notice;
import com.team.RecipeRadar.dto.AddNoticeRequest;
import com.team.RecipeRadar.dto.NoticeResponse;
import com.team.RecipeRadar.dto.UpdateNoticeRequest;
import com.team.RecipeRadar.Service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("/api/admin/notices")
    public ResponseEntity<Notice> addNotice(@RequestBody AddNoticeRequest request) {
        Notice savedNotice = noticeService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedNotice);
    }

    @GetMapping("/api/admin/notices")
    public  ResponseEntity<List<NoticeResponse>> findAllNotices() {
        List<NoticeResponse> notices = noticeService.findAll()
                .stream()
                .map(NoticeResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(notices);
    }
    @GetMapping("api/admin/notices/{id}")
    public  ResponseEntity<NoticeResponse> findNotice(@PathVariable long id) {
        Notice notice = noticeService.findById(id);

        return  ResponseEntity.ok()
                .body(new NoticeResponse(notice));
    }

    @DeleteMapping("/api/admin/notices/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable long id) {
        noticeService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/admin/notices/{id}")
    public  ResponseEntity<Notice> updateNotice(@PathVariable long id, @RequestBody UpdateNoticeRequest request){
        Notice updateNotice = noticeService.update(id, request);

        return ResponseEntity.ok()
                .body(updateNotice);
    }
}
