package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.UpdateNoticeRequest;
import com.team.RecipeRadar.domain.notice.dto.AddNoticeRequest;

import java.util.List;

public interface NoticeService {
    Notice save(AddNoticeRequest request);

    List<Notice> findAll();

    Notice findById(long id);

    void delete(long id);

    Notice update(long id, UpdateNoticeRequest request);
}
