package com.team.RecipeRadar.domain.notice.application.admin;

import com.team.RecipeRadar.domain.notice.dto.request.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.request.AdminUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminNoticeService {

    void save(AdminAddRequest adminAddNoticeDto, Long memberId, MultipartFile file);

    void update(Long noticeId, AdminUpdateRequest adminUpdateNoticeDto, MultipartFile file);

    void delete(List<Long> noticeIds);
}
