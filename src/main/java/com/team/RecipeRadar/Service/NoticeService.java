package com.team.RecipeRadar.Service;

import com.team.RecipeRadar.Entity.Notice;
import com.team.RecipeRadar.dto.AddNoticeRequest;
import com.team.RecipeRadar.dto.UpdateNoticeRequest;

import java.util.List;

public interface NoticeService {
    Notice save(AddNoticeRequest request);

    List<Notice> findAll();

    Notice findById(long id);

    void delete(long id);

    Notice update(long id, UpdateNoticeRequest request);
}
