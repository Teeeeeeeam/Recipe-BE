package com.team.RecipeRadar.domain.member.dao;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(QueryDslConfig.class)
@Transactional
@ActiveProfiles("test")
@Slf4j
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("아이디 찾기 테스트")
    void findById(){

        String email = "test@email.com";
        Member member = Member.builder().username("유저네임").nickName("닉네임").loginId("testId").email(email).login_type("normal").build();
        Member member1 = Member.builder().username("유저네임").nickName("닉네임11").loginId("testId11").email(email).login_type("kakao").build();

        memberRepository.save(member);
        memberRepository.save(member1);

        List<Member> findId = memberRepository.findByUsernameAndEmail("유저네임",email);

        assertThat(findId.size()).isEqualTo(2);
        assertThat(findId.get(0).getEmail()).isEqualTo(email);
        assertThat(findId.get(0).getLogin_type()).isEqualTo("normal");

        assertThat(findId.get(1).getEmail()).isEqualTo(email);
        assertThat(findId.get(1).getLogin_type()).isEqualTo("kakao");
    }
    
    @Test
    @DisplayName("비밀번호 찾기 테스트")
    void findByPwd(){
        String email="test@email.com";
        String loginId = "testId";
        String username = "username";

        Member member = Member.builder().username(username).loginId(loginId).email(email).build();
        memberRepository.save(member);

        Boolean aBoolean = memberRepository.existsByUsernameAndLoginIdAndEmail(username, loginId, email);
        assertThat(aBoolean).isTrue();

        Boolean aBoolean1 = memberRepository.existsByUsernameAndLoginIdAndEmail(username, "1233", email);
        assertThat(aBoolean1).isFalse();

    }

    @Test
    @DisplayName("가입한 회원 모두 조회_무한 페이징")
    void findAllMembers(){
        String loginId = "loginId";
        Member member_1 = Member.builder().username("회원1").email("email1").loginId(loginId).nickName("닉네임1").join_date(LocalDate.now()).build();
        Member member_2 = Member.builder().username("회원2").email("email2").loginId("loginId2").nickName("닉네임2").join_date(LocalDate.now()).build();

        Member save = memberRepository.save(member_1);
        memberRepository.save(member_2);

        Pageable pageRequest = PageRequest.of(0, 1);
        Slice<MemberDto> memberInfo = memberRepository.getMemberInfo(null,pageRequest);

        assertThat(memberInfo.getContent()).hasSize(1);
        assertThat(memberInfo.hasNext()).isTrue();
        assertThat(memberInfo.getContent().get(0).getLoginId()).isEqualTo(loginId);
    }


    @Test
    @DisplayName("가입한 회원 검색_무한 페이징")
    void searchMember(){

        String meme1_loginId = "loginId";
        String meme2_loginId = "loginId";
        Member member_1 = Member.builder().username("회원1").email("email1").loginId(meme1_loginId).nickName("닉네임1").join_date(LocalDate.now()).build();
        Member member_2 = Member.builder().username("회원2").email("email2").loginId(meme2_loginId).nickName("닉네임2").join_date(LocalDate.now()).build();

        memberRepository.save(member_1);
        memberRepository.save(member_2);

        Pageable request = PageRequest.of(0, 1);
        Slice<MemberDto> findMember_1 = memberRepository.searchMember(meme1_loginId, null, null, null,null, request);
        Slice<MemberDto> findMember_2 = memberRepository.searchMember(meme2_loginId, "닉네임2", null, null,null, request);
        Slice<MemberDto> memberDtos = memberRepository.searchMember(meme1_loginId, "닉네임2", null, null, null,request);


        assertThat(findMember_1.getContent().get(0).getLoginId()).isEqualTo(meme1_loginId);     //첫번째 회원
        assertThat(findMember_2.getContent().get(0).getLoginId()).isEqualTo(meme2_loginId);     // 두번쨰 회원
        assertThat(memberDtos.hasNext()).isFalse();
        assertThat(memberDtos.getContent()).hasSize(1);
    }

}