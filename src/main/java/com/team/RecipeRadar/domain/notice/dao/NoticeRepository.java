package com.team.RecipeRadar.domain.notice.dao;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    void deleteByMember_Id(Long memberId);
}
