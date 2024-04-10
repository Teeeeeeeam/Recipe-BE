package com.team.RecipeRadar.domain.post.dao;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfig.class)
@Transactional
class PostRepositoryTest {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;


    @Test
    @DisplayName("사용자페이지- 작성한 게시글 페이징 처리 테스트")
    void userInfo_Post_get(){

        Member member = memberRepository.save(Member.builder().loginId("testId").build());

        Member member1 = memberRepository.save(Member.builder().loginId("testId1").build());

        Post post1 = postRepository.save(Post.builder().postContent("테스트 게시글1").postTitle("타이틀1").postServing("서블").postCookingTime("이름").postCookingLevel("레벨").member(member).postLikeCount(0).build());
        Post post2 = postRepository.save(Post.builder().postContent("테스트 게시글2").postTitle("타이틀2").postServing("서블2").postCookingTime("이름2").postCookingLevel("레벨2").member(member).postLikeCount(0).build());
        postRepository.save(Post.builder().postContent("테스트 게시글3").postTitle("타이틀3").postServing("서블3").postCookingTime("이름3").postCookingLevel("레벨3").member(member1).postLikeCount(0).build());

        Pageable pageable = PageRequest.of(0, 2);

        Slice<UserInfoPostRequest> userInfoPostDtos = postRepository.userInfoPost(member.getId(), pageable);

        List<UserInfoPostRequest> content = userInfoPostDtos.getContent();

        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(0).getPostTitle()).isEqualTo(post1.getPostTitle());
        assertThat(content.get(1).getPostTitle()).isEqualTo(post2.getPostTitle());
        assertThat(userInfoPostDtos.hasNext()).isFalse();

        Pageable pageable1 = PageRequest.of(1, 2);

        Slice<UserInfoPostRequest> userInfoPostDtos1 = postRepository.userInfoPost(member.getId(), pageable1);
        assertThat(userInfoPostDtos1.getContent()).isEmpty();

    }
}