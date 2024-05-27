package com.team.RecipeRadar.domain.notice.dao;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {
    List<Notice> findByNoticeTitleContainingIgnoreCase(String noticeTitle);

    @Modifying
    @Query("delete from Notice c where c.member.id=:member_id and c.id=:notice_id")
    void deleteByMemberId(@Param("member_id") Long member_id,@Param("notice_id")Long notice_id);
}
