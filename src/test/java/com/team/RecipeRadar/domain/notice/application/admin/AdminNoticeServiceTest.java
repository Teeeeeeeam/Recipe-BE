package com.team.RecipeRadar.domain.notice.application.admin;

import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.request.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.request.AdminUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminNoticeServiceTest {

    @Mock NoticeRepository noticeRepository;
    @Mock MemberRepository memberRepository;
    @Mock ImgRepository imgRepository;
    @Mock S3UploadService s3UploadService;

    @InjectMocks AdminNoticeServiceImpl adminNoticeService;

    private final Long memberId = 1l;
    private final String originalName = "test.png";

    private Member member;
    private Notice notice;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(memberId).nickName("사용자").build();
        notice = Notice.builder().id(1L).noticeContent("내용").noticeTitle("제목").member(member).build();
        file = new MockMultipartFile("사진", originalName, "image/jpeg", "Test".getBytes());
    }

    @Test
    @DisplayName("공지사항 저장")
    void saveNotice() {
        AdminAddRequest adminAddRequest = new AdminAddRequest();
        adminAddRequest.setNoticeTitle("제목");
        adminAddRequest.setNoticeContent("내용");

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(noticeRepository.save(any(Notice.class))).thenReturn(notice);

        adminNoticeService.save(adminAddRequest, memberId, file);

        verify(s3UploadService, times(1)).uploadFile(any(), anyList());
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @Test
    @DisplayName("공지사항 삭제")
    void delete_notice() {
        List<Notice> noticeList = List.of(notice, notice);

        noticeList.forEach(notice -> {
            UploadFile uploadFile = UploadFile.createUploadFile(List.of(notice), originalName, "str");
            when(imgRepository.findByNoticeId(anyLong())).thenReturn(uploadFile);
        });

        when(noticeRepository.findAllById(anyList())).thenReturn(noticeList);

        adminNoticeService.delete(anyList());

        verify(noticeRepository, times(2)).delete(any(Notice.class));
        verify(imgRepository, times(2)).deleteByNoticeId(anyLong());
    }

    @Test
    @DisplayName("공지사항 수정")
    void update_notice() {
        AdminUpdateRequest adminUpdateRequest = new AdminUpdateRequest();
        adminUpdateRequest.setNoticeContent("변경 내용");
        adminUpdateRequest.setNoticeTitle("변경 제목");

        when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(notice));

        adminNoticeService.update(anyLong(), adminUpdateRequest, file);

        assertThat(notice.getNoticeContent()).isEqualTo("변경 내용");
        assertThat(notice.getNoticeTitle()).isEqualTo("변경 제목");
    }
}