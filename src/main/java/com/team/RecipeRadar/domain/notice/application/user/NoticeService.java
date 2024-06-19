package com.team.RecipeRadar.domain.notice.application.user;

import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.response.InfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.response.InfoNoticeResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeService {

    List<NoticeDto> mainNotice();

    InfoNoticeResponse noticeInfo(Long noticeId, Pageable pageable);

    InfoDetailsResponse detailNotice(Long noticeId);

    InfoNoticeResponse searchNoticeWithTitle(String title, Long lastId, Pageable pageable);
}
