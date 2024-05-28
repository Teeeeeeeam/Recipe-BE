package com.team.RecipeRadar.domain.notice.dao;

import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepositoryCustom {

    List<NoticeDto> mainNotice();
}
