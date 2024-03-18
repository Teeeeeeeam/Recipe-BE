package com.team.RecipeRadar.service;

import com.team.RecipeRadar.Entity.Notice;
import com.team.RecipeRadar.dto.AddNoticeRequest;
import com.team.RecipeRadar.dto.UpdateNoticeRequest;
import com.team.RecipeRadar.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Notice save(AddNoticeRequest request) {
        return noticeRepository.save(request.toEntity());
    }

    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    public Notice findById(long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(long id) {
        noticeRepository.deleteById(id);
    }

    @Transactional
    public Notice update(long id, UpdateNoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        notice.update(request.getNoticeTitle(), request.getNoticeContent());

        return notice;
    }
}
