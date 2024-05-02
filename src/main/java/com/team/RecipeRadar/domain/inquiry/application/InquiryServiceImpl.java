package com.team.RecipeRadar.domain.inquiry.application;

import com.team.RecipeRadar.domain.inquiry.dao.InquiryRepository;
import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserAddInquiryDto;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserDeleteInquiryDto;
import com.team.RecipeRadar.domain.inquiry.exception.InquiryException;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
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
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    @Override
    public Inquiry save(UserAddInquiryDto userAddInquiryDto) {
        Long memberId = userAddInquiryDto.getMemberId();

        Optional<Member> member = memberRepository.findById(memberId);

        if(member.isPresent()) {
            Member member1 = member.get();
            LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);//yyy-dd-mm:hh-MM으로 저장 밀리세컨트는 모두 0초
            Inquiry build = Inquiry.builder()
                    .inquiryTitle(userAddInquiryDto.getInquiryTitle())
                    .member(member1)
                    .created_at(LocalDateTime.now())
                    .build();
        return inquiryRepository.save(build);
        } else {
            throw new NoSuchElementException("문의사항 저장에 실패했습니다.");
        }

    }

    @Override
    public List<Inquiry> findAll() {
        try {
            return inquiryRepository.findAll();
        } catch (DataAccessException e) {
            throw new InquiryException("문의사항 조회에 실패했습니다.");
        }
    }

    @Override
    public Inquiry findById(long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryException("not found: " + id));
    }

    @Override
    public void delete(UserDeleteInquiryDto userDeleteInquiryDto) {

        Long memberDtoId = userDeleteInquiryDto.getMemberId();

        Member member = getMemberThrows(memberDtoId);

        if(member.getId().equals(memberDtoId)) {
            inquiryRepository.deleteById(member.getId());
        } else
            throw new InquiryException("문의사항 삭제에 실패했습니다.");
    }

    @Override
    public void update(Long memberId, Long inquiryId, String inquiryTitle) {

        Member member = getMemberThrows(memberId);
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new NoSuchElementException("해당 게시물을 찾을수 없습니다."));
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);

        if(inquiry.getMember().equals(member)){
            inquiry.update(inquiryTitle);
            inquiry.updateTime(localDateTime);
        } else
            throw new InquiryException("작성자만 수정 가능합니다.");

    }

    private Member getMemberThrows(Long member_id) {
        return memberRepository.findById(member_id).orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을수 없습니다."));
    }
}
