package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.*;
import com.team.RecipeRadar.domain.notice.dto.info.InfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.info.InfoNoticeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoticeService {
    void save(AdminAddRequest adminAddNoticeDto,Long memberId, MultipartFile file);

    void delete(List<Long> noticeIds);

    void update(Long noticeId, AdminUpdateRequest adminUpdateNoticeDto, MultipartFile file);

    List<NoticeDto> mainNotice();

    InfoNoticeResponse Notice(Long noticeId, Pageable pageable);

    InfoDetailsResponse detailNotice(Long noticeId);
}
