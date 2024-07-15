package com.team.RecipeRadar.domain.notice.application.user;

import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import com.team.RecipeRadar.domain.notice.dto.response.InfoDetailsResponse;
import com.team.RecipeRadar.domain.notice.dto.response.InfoNoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;


    @Override
    public List<NoticeDto> mainNotice() {
        return noticeRepository.mainNotice();
    }

    @Override
    public InfoNoticeResponse noticeInfo(Long noticeId, Pageable pageable) {
        Slice<NoticeDto> noticeDto = noticeRepository.adminNotice(noticeId,pageable);

        return new InfoNoticeResponse(noticeDto.hasNext(),noticeDto.getContent());
    }

    @Override
    public InfoDetailsResponse detailNotice(Long noticeId) {
        NoticeDto noticeDto = noticeRepository.detailsPage(noticeId);
        return InfoDetailsResponse.of(noticeDto);
    }

    @Override
    public InfoNoticeResponse searchNoticeWithTitle(String title, Long lastId, Pageable pageable) {
        Slice<NoticeDto> slice = noticeRepository.searchNotice(title, lastId, pageable);
        return new InfoNoticeResponse(slice.hasNext(),slice.getContent());
    }

}
