package com.team.RecipeRadar.domain.member.dao;

import com.team.RecipeRadar.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long>,CustomMemberRepository {

    Member findByLoginId(String loginId);

    @Query("select m from Member m where  binary(m.loginId)=:loginId")      //MySQL의 BINARY 타입을 사용해 대소문자를 구문하기 위한 쿼리 사용
    Member findByCaseSensitiveLoginId(@Param("loginId") String loginId);

    boolean existsByLoginId(String loginId);
    List<Member> findByEmail(String email);

    List<Member> findByUsernameAndEmail(String username, String email);

    Boolean existsByUsernameAndLoginIdAndEmail(String username, String loginId, String email);

    Boolean existsByNickName(String nickname);

    void deleteById(Long memberId);

    long countAllBy();
}
