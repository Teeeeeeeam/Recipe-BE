package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddRequest;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminUpdateRequest;
import com.team.RecipeRadar.domain.notice.dto.info.InfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.info.InfoNoticeResponse;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDto> mainNotice() {
        return noticeRepository.mainNotice();
    }

    @Override
    @Transactional(readOnly = true)
    public InfoNoticeResponse noticeInfo(Long noticeId, Pageable pageable) {
        Slice<NoticeDto> noticeDto = noticeRepository.adminNotice(noticeId,pageable);

        return new InfoNoticeResponse(noticeDto.hasNext(),noticeDto.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public InfoDetailsResponse detailNotice(Long noticeId) {
        NoticeDto noticeDto = noticeRepository.detailsPage(noticeId);
        return InfoDetailsResponse.of(noticeDto);
    }

    @Override
    @Transactional(readOnly = true)
    public InfoNoticeResponse searchNoticeWithTitle(String title, Long lastId, Pageable pageable) {
        Slice<NoticeDto> slice = noticeRepository.searchNotice(title, lastId, pageable);
        return new InfoNoticeResponse(slice.hasNext(),slice.getContent());
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
