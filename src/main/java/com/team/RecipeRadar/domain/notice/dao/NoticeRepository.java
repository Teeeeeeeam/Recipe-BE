package com.team.RecipeRadar.domain.notice.dao;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {


}
