package com.team.RecipeRadar.domain.notice.dao;

import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepositoryCustom {

    List<NoticeDto> mainNotice();

    Slice<NoticeDto> adminNotice(Long noticeId,Pageable pageable);
}
