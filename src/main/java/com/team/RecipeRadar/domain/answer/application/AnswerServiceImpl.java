package com.team.RecipeRadar.domain.answer.application;

import com.team.RecipeRadar.domain.answer.dao.AnswerRepository;
import com.team.RecipeRadar.domain.answer.domain.Answer;
import com.team.RecipeRadar.domain.answer.dto.admin.AdminAddAnswerDto;
import com.team.RecipeRadar.domain.answer.dto.admin.AdminDeleteAnswerDto;
import com.team.RecipeRadar.domain.answer.exception.AnswerException;
import com.team.RecipeRadar.domain.inquiry.dao.InquiryRepository;
import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class AnswerServiceImpl implements AnswerService{

    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final InquiryRepository inquiryRepository;
    /**
     * 응답 작성하는 기능 -> 문의사항과 사용자의 정보를 이용해 Commnet 객체를 생성후 저장
     * @param adminAddAnswerDto
     * @return 저장된 Commnet객체
     */
    public Answer save(AdminAddAnswerDto adminAddAnswerDto) {
        Long memberId = adminAddAnswerDto.getMemberId();
        Long inquiryId = adminAddAnswerDto.getInquiryId();

        Optional<Member> member = memberRepository.findById(memberId);
        Optional<Inquiry> inquiryOptional = inquiryRepository.findById(inquiryId);

        if (member.isPresent() && inquiryOptional.isPresent()) {        //사용자 정보와 문의사항 정보가 존재할시에만 통과
            Member member1 = member.get();
            Inquiry inquiry = inquiryOptional.get();
            LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);        //yyy-dd-mm:hh-MM으로 저장 밀리세컨트는 모두 0초
            Answer build = Answer.builder()                               //응답 저장
                    .answerContent(adminAddAnswerDto.getAnswerContent())
                    .member(member1)
                    .inquiry(inquiry)
                    .created_at(localDateTime)
                    .build();
            return answerRepository.save(build);
        } else {
            throw new NoSuchElementException("회원정보나 게시글을 찾을수 없습니다.");     //사용자 및 문의사항이 없을시에는 해당 예외발생
        }
    }

    @Override
    public Answer findById(Long id) {
        return answerRepository.findById(id).orElseThrow(() -> new AnswerException("찾을 수 없습니다."));
    }

    /**
     * 응답의 Id와 사용자의 Id를 사용해서 응답을 삭제한다.
     * 응답의 작성자가 아닐경우 삭제시에는 ->AnswerException 예외를 날린다.
     * @param adminDeleteAnswerDto
     */
    @Override
    public void delete_answer(AdminDeleteAnswerDto adminDeleteAnswerDto) {

        Long memberDtoId = adminDeleteAnswerDto.getMemberId();
        Long answerDtoId = adminDeleteAnswerDto.getAnswerId();

        Member member = getMemberThrows(memberDtoId);
        Answer answer = answerRepository.findById(answerDtoId).orElseThrow(() -> new NoSuchElementException("해당 응답을 찾을 수없습니다. " + answerDtoId));

        if (answer.getMember().getId().equals(memberDtoId)){           // 응답을 등록한 사용자 일경우
            answerRepository.deleteMemberId(member.getId(),answer.getId());
        }else
            throw new AnswerException("작성자만 삭제할수 있습니다.");      //응답을 동락한 사용자가 아닐시
    }

    /**
     * 응답 수정 기능 -> 작성자만 응답을 수정가능하다.
     * @param member_id     사용자 id
     * @param answer_id    응답 id
     * @param answer_content 수정할 응답 내용
     */
    @Override
    public void update(Long member_id,Long answer_id, String answer_content) {

        Member member = getMemberThrows(member_id);
        Answer answer = answerRepository.findById(answer_id).orElseThrow(() -> new NoSuchElementException("해당 응답을 찾을수 없습니다."));
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);

        if (answer.getMember().equals(member)){        //Answer 엔티티에 Mmeber가 있는지 없는지 확인
            answer.update(answer_content);
            answer.updateTime(localDateTime);
        }else
            throw new AnswerException("작성자만 수정 가능합니다.");
    }
    

    private Member getMemberThrows(Long member_id) {
        return memberRepository.findById(member_id).orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을수 없습니다."));
    }
    
}
