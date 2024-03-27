package com.team.RecipeRadar.domain.member.dao;

import com.team.RecipeRadar.domain.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
@Slf4j
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
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

}