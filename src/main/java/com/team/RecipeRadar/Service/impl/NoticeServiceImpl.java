package com.team.RecipeRadar.service.impl;

import com.team.RecipeRadar.Entity.Notice;
import com.team.RecipeRadar.dto.AddNoticeRequest;
import com.team.RecipeRadar.dto.UpdateNoticeRequest;
import com.team.RecipeRadar.repository.NoticeRepository;
import com.team.RecipeRadar.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public Notice save(AddNoticeRequest request) {
        return noticeRepository.save(request.toEntity());
    }

    @Override
    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    @Override
    public Notice findById(long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    @Override
    public void delete(long id) {
        noticeRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Notice update(long id, UpdateNoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        notice.update(request.getNoticeTitle(), request.getNoticeContent());

        return notice;
    }
}
