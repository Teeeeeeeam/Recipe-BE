package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddNoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminDeleteNoticeDto;

import java.util.List;

public interface NoticeService {
    Notice save(AdminAddNoticeDto adminAddNoticeDto);

    List<Notice> findAll();

    Notice findById(long id);

    void delete(AdminDeleteNoticeDto admindeleteNoticeDto);

    void update(Long memberId, Long noticeId, String noticeTitle);
}
