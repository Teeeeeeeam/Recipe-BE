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
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final MemberRepository memberRepository;
    private final NoticeRepository noticeRepository;
    private final S3UploadService s3UploadService;
    private final ImgRepository imgRepository;

    /**
     * 공지사항 저장을 저장하는 메서드
     */
    @Override
    public void save(AdminAddRequest adminAddNoticeDto, Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));

        Notice notice = noticeRepository.save(Notice.createNotice(adminAddNoticeDto.getNoticeTitle(), adminAddNoticeDto.getNoticeContent(),member));

        if(file!=null)
            s3UploadService.uploadFile(file,List.of(notice));
    }

    /**
     * 공지사항을 삭제하는 메서드
     */
    @Override
    public void delete(List<Long> noticeIds) {

        List<Notice> notices = noticeRepository.findAllById(noticeIds);
        if(notices.isEmpty()) throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_NOTICE);

        notices.forEach( notice -> {
            deleteUploadFile(notice);
            noticeRepository.delete(notice);
        });
    }

    /**
     * 공지사항을 수정하는 로직
     */
    @Override
    public void update(Long noticeId, AdminUpdateRequest adminUpdateRequest, MultipartFile file) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_NOTICE));

        s3UploadService.updateFile(file,List.of(notice));

        notice.update(adminUpdateRequest.getNoticeTitle(), adminUpdateRequest.getNoticeContent());
    }

    /**
     * 이미지를 삭제 하는 메서드
     * s3버킷에 저장된 이미지 파일과 upload에 저장된 파일을 삭제합니다.
     */
    private void deleteUploadFile(Notice notice) {
        UploadFile uploadFile = imgRepository.findByNoticeId(notice.getId());
        if (existsFile(uploadFile)) {
            deleteS3Image(uploadFile);
            imgRepository.deleteNoticeId(notice.getId());
        }
    }

    /* S3 에서 이미지 삭제*/
    private void deleteS3Image(UploadFile uploadFile) {
        s3UploadService.deleteFile(uploadFile.getStoreFileName());
    }

    /* 이미지가 존재하는지 검증 */
    private boolean existsFile(UploadFile uploadFile) {
        return uploadFile != null && uploadFile.getStoreFileName() != null;
    }

}
