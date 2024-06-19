package com.team.RecipeRadar.domain.qna.application.admin;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.qna.dao.question.QuestionRepository;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminQnAServiceTest {

    @Mock QuestionRepository questionRepository;
    @Mock MemberRepository memberRepository;

    @InjectMocks AdminQnAServiceImpl questionService;

    @Test
    @DisplayName("관리자만 문의사항 상세보기 접근 가능")
    void detailAdmin_Question() {
        Long questionId = 1L;
        String loginId = "admin";
        Member adminMember = Member.builder().id(1l).loginId(loginId).roles("ROLE_ADMIN").build();

        QuestionDto questionDto = new QuestionDto();

        when(memberRepository.findById(1l)).thenReturn(Optional.of(adminMember));
        when(questionRepository.details(questionId)).thenReturn(questionDto);

        QuestionDto result = questionService.detailAdminQuestion(questionId,1l);

        assertThat(result).isEqualTo(questionDto);
    }

    @Test
    @DisplayName("관리자만 문의사항 상세보기 접근 가능 - 예외 발생")
    void detailAdmin_Question_throwsException() {
        Long questionId = 1L;
        String loginId = "user";
        Member userMember = Member.builder().id(1l).loginId(loginId).roles("ROLE_USER").build();

        when(memberRepository.findById(1l)).thenReturn(Optional.of(userMember));

        assertThatThrownBy(() -> questionService.detailAdminQuestion(questionId, 1l))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("관리자만 접근 가능 가능합니다.");
    }

}