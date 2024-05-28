package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminUpdateRequest;
import com.team.RecipeRadar.global.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
     * @param loginId   로그인한 사용자의 loginId
     * @param noticeId    삭제할 공지사항 id
     */
    @Override
    public void delete(String loginId, Long noticeId) {

        Member member = memberRepository.findByLoginId(loginId);
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchElementException("공지사항을 찾을수 없습니다."));
        if(!notice.getMember().getLoginId().equals(member.getLoginId())) throw new AccessDeniedException("관리자만 삭제할수 있습니다.");

        UploadFile byNoticeOriginalFileName = imgRepository.getByNoticeOriginalFileName(notice.getId());
        s3UploadService.deleteFile(byNoticeOriginalFileName.getStoreFileName());
        imgRepository.deleteNoticeId(notice.getId());
        noticeRepository.deleteByMemberId(member.getId(),noticeId);
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
}
