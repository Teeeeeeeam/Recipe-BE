package com.team.RecipeRadar.domain.member.dao;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(QueryDslConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    private List<Member> member;

    @BeforeEach
    void setUp(){
        member = List.of(
                Member.builder().username("유저네임").nickName("닉네임").loginId("testId").email("test@eamil.com").login_type("normal").build(),
                Member.builder().username("유저네임1").nickName("닉네임1").loginId("testId1").email("test1@eamil.com").login_type("normal").build(),
                Member.builder().username("어드민1").nickName("어드민1").loginId("admin1").email("admin1@eamil.com").roles("ROLE_ADMIN").login_type("normal").build(),
                Member.builder().username("어드민2").nickName("어드민2").loginId("admin2").email("admin2@eamil.com").roles("ROLE_ADMIN").login_type("normal").build(),
                Member.builder().username("유저네임").nickName("카카오").loginId("카카오아이디").email("test@eamil.com").login_type("social").build()
        );
        memberRepository.saveAll(member);
    }
    @Test
    @DisplayName("아이디 찾기 테스트")
    void findById(){
        List<Member> findId = memberRepository.findByUsernameAndEmail("유저네임","test@eamil.com");

        assertThat(findId).hasSize(2);
        assertThat(findId.get(1).getNickName()).isEqualTo("카카오");
    }
    
    @Test
    @DisplayName("비밀번호 찾기 테스트")
    void findByPwd(){
        String email="test@eamil.com";
        String loginId = "testId";
        String username = "유저네임";

        Boolean aBoolean = memberRepository.existsByUsernameAndLoginIdAndEmail(username, loginId, email);
        assertThat(aBoolean).isTrue();

        Boolean aBoolean1 = memberRepository.existsByUsernameAndLoginIdAndEmail(username, "1233", email);
        assertThat(aBoolean1).isFalse();

    }

    @Test
    @DisplayName("이메일 조회")
    void findByEmail(){
        List<Member> byEmail = memberRepository.findByEmail("test@eamil.com");
        assertThat(byEmail).isNotEmpty();
        assertThat(byEmail).hasSize(2);
    }

    @Test
    @DisplayName("가입한 회원 모두 조회_무한 페이징")
    void findAllMembers(){
        Slice<MemberDto> memberInfo = memberRepository.getMemberInfo(null,Pageable.ofSize(1));

        assertThat(memberInfo.getContent()).hasSize(1);
        assertThat(memberInfo.hasNext()).isTrue();
    }
    
    @Test
    @DisplayName("닉네임이 존재하는 여부 테스트")
    void existsByNickName(){
        Boolean existsByNickName = memberRepository.existsByNickName("닉네임");
        Boolean existsByNickName1 = memberRepository.existsByNickName("없는 닉네임");

        assertThat(existsByNickName).isTrue();
        assertThat(existsByNickName1).isFalse();
    }
    
    @Test
    @DisplayName("사용자 삭제 테스트")
    void deleteByMemberId(){
        List<Member> before = memberRepository.findAll();
        memberRepository.deleteById(member.get(0).getId());
        List<Member> after = memberRepository.findAll();
        assertThat(before).hasSize(5);
        assertThat(after).hasSize(4);
    }

    @Test
    @DisplayName("사용자 수 조회")
    void countMember(){
        long count = memberRepository.countAllBy();
        assertThat(count).isEqualTo(5l);
    }

    @Test
    @DisplayName("가입한 회원 검색_무한 페이징")
    void searchMember(){

        Slice<MemberDto> searchedMember = memberRepository.searchMember("loginId", null, null, null,null, Pageable.ofSize(3));
        Slice<MemberDto> searchMember = memberRepository.searchMember(null, "닉네", null, null,null, Pageable.ofSize(3));

        assertThat(searchedMember).isEmpty();
        assertThat(searchMember).hasSize(2);
    }
    
    @Test
    @DisplayName("어드민 사용자 조회")
    void findAdminMember(){
        List<Member> members = memberRepository.adminMember();
        assertThat(members).hasSize(2);
        assertThat(members.get(0).getNickName()).isEqualTo("어드민1");
        assertThat(members.get(0).getNickName().getClass()).isEqualTo(String.class);
    }

}