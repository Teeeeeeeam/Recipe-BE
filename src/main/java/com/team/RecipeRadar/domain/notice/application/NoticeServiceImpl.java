package com.team.RecipeRadar.domain.notice.application;

import com.team.RecipeRadar.domain.inquiry.dto.user.UserDeleteInquiryDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminAddNoticeDto;
import com.team.RecipeRadar.domain.notice.dto.admin.AdminDeleteNoticeDto;
import com.team.RecipeRadar.domain.notice.exception.NoticeException;
import com.team.RecipeRadar.domain.notice.exception.ex.AccessDeniedNoticeException;
import com.team.RecipeRadar.domain.notice.exception.ex.NoticeNotFoundException;
import com.team.RecipeRadar.domain.notice.exception.ex.UnauthorizedNoticeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Override
    public Notice save(AdminAddNoticeDto adminAddNoticeDto) {
        Long memberId = adminAddNoticeDto.getMemberId();
        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isPresent()) {
            Member member1 = member.get();
            LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);//yyy-dd-mm:hh-MM으로 저장 밀리세컨트는 모두 0초
            Notice build = Notice.builder()
                    .noticeTitle(adminAddNoticeDto.getNoticeTitle())
                    .member(member1)
                    .created_at(LocalDateTime.now())
                    .build();
            return noticeRepository.save(build);
        } else {
            throw new NoSuchElementException("공지사항 저장에 실패했습니다.");
        }
    }

    @Override
    public List<Notice> findAll() {
        try {
            return noticeRepository.findAll();
        } catch (DataAccessException e) {
            // 데이터베이스에서 모든 공지사항을 가져오는 중에 문제가 발생한 경우
            throw new NoticeException("공지사항 조회에 실패했습니다.");
        }
    }

    @Override
    public Notice findById(long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeException("찾을 수 없습니다." + id));
    }

    @Override
    public void delete(AdminDeleteNoticeDto adminDeleteNoticeDto) {

        Long memberDtoId = adminDeleteNoticeDto.getMemberId();

        Member member = getMemberThrows(memberDtoId);

        if(member.getId().equals(memberDtoId)) {
            noticeRepository.deleteById(member.getId());
        } else
            throw new NoticeException("공지사항 삭제에 실패했습니다.");
    }

    @Override
    public void update(Long memberId, Long noticeId, String noticeTitle) {

        Member member = getMemberThrows(memberId);
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new NoSuchElementException("해당 공지사항을 찾을수 없습니다."));
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);

        if(notice.getMember().equals(member)) {
            notice.update(noticeTitle);
            notice.updateTime(localDateTime);
        } else
            throw new NoticeException("작성자만 수정 가능합니다.");
    }

    private Member getMemberThrows(Long member_id) {
        return memberRepository.findById(member_id).orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을수 없습니다."));
    }
}
