package com.team.RecipeRadar.domain.inquiry.application;

import com.team.RecipeRadar.domain.inquiry.dao.InquiryRepository;
import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserAddRequest;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserDeleteRequest;
import com.team.RecipeRadar.domain.inquiry.dto.user.UserUpdateRequest;
import com.team.RecipeRadar.domain.inquiry.dto.user.ValidInquiryRequest;
import com.team.RecipeRadar.domain.inquiry.exception.InquiryException;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.user.ValidPostRequest;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    /**
     * 문의사항 저장을 저장하는 로직
     * @param userAddInquiryDto
     */
    @Override
    public void save(UserAddRequest userAddInquiryDto) {
        Long memberId = userAddInquiryDto.getMemberId();

        Optional<Member> op_member = memberRepository.findById(memberId);

        if(op_member.isPresent()) {
            Member member= op_member.get();

            Inquiry inquiry = Inquiry.builder()
                    .inquiryTitle(userAddInquiryDto.getInquiryTitle())
                    .inquiryContent(userAddInquiryDto.getInquiryContent())
                    .member(member)
                    .inquiryPassword(passwordEncoder.encode(userAddInquiryDto.getInquiryPassword()))
                    .created_at(LocalDateTime.now())
                    .build();
            inquiryRepository.save(inquiry);
        } else {
            // 데이터베이스 저장 중에 문제가 발생한 경우
            throw new NoSuchElementException("문의사항 저장에 실패했습니다.");
        }
    }

    /**
     * 게시글을 삭제하는 로직
     * @param loginId   로그인한 사용자의 loginId
     * @param inquiryId    삭제할 문의사항 id
     */
    @Override
    public void delete(String loginId, Long inquiryId) {

        Member member = memberRepository.findByLoginId(loginId);
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new NoSuchElementException("게시글을 찾을수 없습니다."));
        if(!inquiry.getMember().getLoginId().equals(member.getLoginId())) throw new AccessDeniedException("작성자만 삭제할수 있습니다.");

        inquiryRepository.deleteMemberId(member.getId(),inquiryId);
    }

    /**
     * 문의사항을 업데이트 하기 위한 로직
     */
    @Override
    public void update(Long inquiryId, UserUpdateRequest userUpdateRequest, String loginId) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new NoSuchElementException("해당 문의사항을 찾을 수 없습니다."));
        if(!inquiry.getMember().getLoginId().equals(loginId)) throw new AccessDeniedException("작성자만 삭제 가능합니다.");

        inquiry.update(userUpdateRequest.getInquiryTitle(), userUpdateRequest.getInquiryContent(),passwordEncoder.encode(userUpdateRequest.getInquiryPassword()));

        inquiryRepository.save(inquiry);
    }

    /**
     * 문의사항 수정,삭제를하기전에 해당 게시글 등록시 비밀번호 사용해 해당 접근하려는 사용자가 작성한 사용자인지 검증하는 로직
     */
    @Override
    public boolean validInquiryPassword(String loginId, ValidInquiryRequest request) {
        Member byLoginId = memberRepository.findByLoginId(loginId);

        Inquiry inquiry = inquiryRepository.findById(request.getInquiryId()).orElseThrow(() -> new NoSuchElementException("게시글을 찾을수 없습니다."));

        if(!inquiry.getMember().getId().equals(byLoginId.getId()))
            throw new AccessDeniedException("작성한 사용자만 가능합니다.");

        if(!passwordEncoder.matches(request.getPassword(), inquiry.getInquiryPassword()))
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");

        return true;
    }

}
