package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.*;
import com.team.RecipeRadar.domain.notice.dto.info.AdminInfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.info.AdminInfoNoticeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoticeService {
    void save(AdminAddRequest adminAddNoticeDto,String fileUrl,String originalFilename);

    void delete(String loginId, Long noticeId);

    void update(Long noticeId, AdminUpdateRequest adminUpdateNoticeDto, String loginId, MultipartFile file);

    List<NoticeDto> mainNotice();

    AdminInfoNoticeResponse adminNotice(Pageable pageable);

    AdminInfoDetailsResponse adminDetailNotice(Long noticeId);
}
