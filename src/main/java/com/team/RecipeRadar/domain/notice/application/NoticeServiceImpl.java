package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.UpdateNoticeRequest;
import com.team.RecipeRadar.domain.notice.dto.AddNoticeRequest;
import com.team.RecipeRadar.domain.notice.exception.ex.AccessDeniedNoticeException;
import com.team.RecipeRadar.domain.notice.exception.ex.InvalidNoticeRequestException;
import com.team.RecipeRadar.domain.notice.exception.ex.NoticeNotFoundException;
import com.team.RecipeRadar.domain.notice.exception.ex.UnauthorizedNoticeException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public Notice save(AddNoticeRequest request) {

        try{
            return noticeRepository.save(request.toEntity());
        } catch (DataAccessException e) {
            // 데이터베이스 저장 중에 문제가 발생한 경우
            throw new InvalidNoticeRequestException("Failed to save notice", e);
        }
    }

    @Override
    public List<Notice> findAll() {
        try {
            return noticeRepository.findAll();
        } catch (DataAccessException e) {
            // 데이터베이스에서 모든 공지사항을 가져오는 중에 문제가 발생한 경우
            throw new NoticeNotFoundException("Failed to retrieve notices", e);
        }
    }

    @Override
    public Notice findById(long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("not found: " + id));
    }

    @Override
    public void delete(long id) {
        try {
            noticeRepository.deleteById(id);
        } catch (DataAccessException e) {
            // 데이터베이스에서 공지사항을 삭제하는 중에 문제가 발생한 경우
            throw new AccessDeniedNoticeException("Failed to delete notice with id: " + id, e);
        }
    }

    @Transactional
    @Override
    public Notice update(long id, UpdateNoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException(("not found: " + id)));

        try {
            notice.update(request.getNoticeTitle(), request.getNoticeContent());
            return notice;
        } catch (Exception e) {
            // 업데이트하는 중에 문제가 발생한 경우
            throw new UnauthorizedNoticeException("Failed to update notice with id: " + id, e);
        }
    }
}
