package com.team.RecipeRadar.domain.notice.dao;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@Slf4j
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
class NoticeRepositoryTest {

    @Autowired NoticeRepository noticeRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager entityManager;

    @BeforeAll
    @DisplayName("사전 공지사항 데이터 삽입")
    void setData(){
        Member member = memberRepository.save(Member.builder().id(1l).build());
        List<Notice> notices = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            notices.add(Notice.createNotice(i + "번째 제목", "내용", member));
        }
        noticeRepository.saveAll(notices);
    }

    @Test
    @DisplayName("메인 페이지 5개의 공지사항 출력")
    void mainNotice(){
        List<NoticeDto> noticeDtosList = noticeRepository.mainNotice();
        assertThat(noticeDtosList).hasSize(5);
    }

    @Test
    @DisplayName("공지사항 무한 페이징")
    void page(){
        PageRequest request = PageRequest.of(0, 4);
        Slice<NoticeDto> noticeDtos = noticeRepository.adminNotice(null, request);
        assertThat(noticeDtos.hasNext()).isTrue();
        assertThat(noticeDtos.getContent()).hasSize(4);
    }

    @Test
    @DisplayName("공지사항 상세 내용")
    void details_notice(){
        NoticeDto noticeDto = noticeRepository.detailsPage(1l);
        assertThat(noticeDto.getNoticeTitle()).isEqualTo("0번째 제목");
    }

    @Test
    @DisplayName("공지사항 사용자 Id로 삭제")
    void deleteWithMemberId(){
        noticeRepository.deleteMemberId(1l);
    }
    
    @Test
    @DisplayName("제목으로 조회 테스트")
    void searchTitle(){
        PageRequest request = PageRequest.of(0, 10);
        Slice<NoticeDto> noticeDtos = noticeRepository.searchNotice("4", null, request);
        assertThat(noticeDtos.getContent()).hasSize(1);
    }
}