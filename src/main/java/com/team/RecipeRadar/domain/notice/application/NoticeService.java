package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.*;
import com.team.RecipeRadar.domain.notice.dto.info.AdminInfoNoticeResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.util.List;

public interface NoticeService {
    void save(AdminAddRequest adminAddNoticeDto,String fileUrl,String originalFilename);

    void delete(String loginId, Long noticeId);

    void update(Long noticeId, AdminUpdateRequest adminUpdateNoticeDto, String loginId, MultipartFile file);

    List<NoticeDto> mainNotice();

}
