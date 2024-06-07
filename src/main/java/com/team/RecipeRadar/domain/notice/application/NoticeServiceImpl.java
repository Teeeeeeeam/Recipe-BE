package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminUpdateRequest;
import com.team.RecipeRadar.domain.notice.dto.info.InfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.info.InfoNoticeResponse;
import com.team.RecipeRadar.global.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final ImgRepository imgRepository;
    private final S3UploadService s3UploadService;

    /**
     * 공지사항 저장을 저장하는 로직
     * @param adminAddNoticeDto
     */
    @Override
    public void save(AdminAddRequest adminAddNoticeDto,String fileUrl,String originalFilename) {
        Long memberId = adminAddNoticeDto.getMemberId();

        Optional<Member> op_member = memberRepository.findById(memberId);

        if(op_member.isPresent()) {
            Member member = op_member.get();
            LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);


            Notice notice = Notice.builder()
                    .noticeTitle(adminAddNoticeDto.getNoticeTitle())
                    .noticeContent(adminAddNoticeDto.getNoticeContent())
                    .member(member)
                    .created_at(localDateTime)
                    .build();

            Notice notice_save = noticeRepository.save(notice);
            UploadFile uploadFile = UploadFile.builder().originFileName(originalFilename).storeFileName(fileUrl).notice(notice_save).build();
            imgRepository.save(uploadFile);

        } else {
            throw new NoSuchElementException("공지사항 저장에 실패했습니다.");
        }
    }


    /**
     * 공지사항을 삭제하는 로직
     */
    @Override
    public void delete(List<Long> noticeIds) {

        List<Notice> allById = noticeRepository.findAllById(noticeIds);
        if(allById.isEmpty()) throw new BadRequestException("해당 공지 사항이 존재하지 않습니다.");

        for (Notice notice : allById) {
            UploadFile byNoticeId = imgRepository.findByNoticeId(notice.getId());
            if(byNoticeId!=null) {
                s3UploadService.deleteFile(byNoticeId.getStoreFileName());
                imgRepository.deleteNoticeId(notice.getId());
            }
            noticeRepository.delete(notice);
        }
    }

    /**
     * 공지사항을 수정하는 로직
     * @param adminUpdateRequest
     */
    @Override
    public void update(Long noticeId, AdminUpdateRequest adminUpdateRequest, String loginId,MultipartFile file) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchElementException("해당 공지사항을 찾을 수 없습니다."));
        if(!notice.getMember().getLoginId().equals(loginId)) throw new AccessDeniedException("관리자만 삭제 가능합니다.");

        UploadFile uploadFile = imgRepository.getOriginalFileName(notice.getId());
        if(file!=null) {
            if (!uploadFile.getOriginFileName().equals(file.getOriginalFilename())) {       // 원본파일명이 다를경우에만 s3에 기존 사진을 삭제 후 새롭게 저장
                s3UploadService.deleteFile(uploadFile.getStoreFileName());
                String storedFileName = s3UploadService.uploadFile(file);
                uploadFile.update(storedFileName, file.getOriginalFilename());
                imgRepository.save(uploadFile);
            }
        }

        notice.update(adminUpdateRequest.getNoticeTitle(), adminUpdateRequest.getNoticeContent());
        noticeRepository.save(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDto> mainNotice() {
        return noticeRepository.mainNotice();
    }

    @Override
    @Transactional(readOnly = true)
    public InfoNoticeResponse Notice(Long noticeId, Pageable pageable) {
        Slice<NoticeDto> noticeDto = noticeRepository.adminNotice(noticeId,pageable);

        return new InfoNoticeResponse(noticeDto.hasNext(),noticeDto.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public InfoDetailsResponse detailNotice(Long noticeId) {
        NoticeDto noticeDto = noticeRepository.detailsPage(noticeId);
        return InfoDetailsResponse.of(noticeDto);
    }
}
