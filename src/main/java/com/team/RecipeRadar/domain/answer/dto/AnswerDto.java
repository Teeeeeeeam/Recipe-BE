package com.team.RecipeRadar.domain.answer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.answer.domain.Answer;
import com.team.RecipeRadar.domain.inquiry.dto.InquiryDto;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerDto {

    private Long id;

    @Schema(description = "댓글 내용", example = "댓글 작성!")
    private String answer_content;

    @Schema(description = "작성자 닉네임", example = "나만의 냉장고")
    private String nickName;

    private MemberDto member;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private InquiryDto articleDto;

    private LocalDateTime create_at;        //등록일

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updated_at;       //수정일

    public void setLocalDateTime(){
        this.create_at = LocalDateTime.now().withSecond(0).withNano(0);
    }

    public static AnswerDto of(Answer answer){
        return AnswerDto.builder()
                .id(answer.getId())
                .answer_content(answer.getAnswerContent())
                .nickName(answer.getMember().getNickName())
                .create_at(answer.getCreated_at())
                .updated_at(answer.getUpdated_at()).build();
    }

    public static AnswerDto admin(Answer answer){
        Member member = answer.getMember();
        MemberDto memberDto = MemberDto.builder().loginId(member.getLoginId()).nickname(member.getNickName()).username(member.getUsername()).build();
        return AnswerDto.builder()
                .id(answer.getId())
                .answer_content(answer.getAnswerContent())
                .create_at(answer.getCreated_at())
                .member(memberDto).build();
    }
}
