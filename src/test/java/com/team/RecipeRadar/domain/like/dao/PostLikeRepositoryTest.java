package com.team.RecipeRadar.domain.like.dao;

import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfig.class)
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class PostLikeRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void init() {
        Member member = Member.builder().loginId("testId").build();
        Post post = Post.builder().postContent("테스트 게시글").postTitle("타이틀").postServing("서블").postCookingTime("이름").postCookingLevel("레벨").build();

        Member savedMember = memberRepository.save(member);
        Post savedPost = postRepository.save(post);

        PostLike postLike = PostLike.builder().post(savedPost).member(savedMember).build();
        postLikeRepository.save(postLike);
    }

    @Test
    @DisplayName("게시글 좋아요 엔티티 저장")
    void save_postLikeEntity() {
        // 좋아요 엔티티 저장 확인
        Optional<PostLike> postLike = postLikeRepository.findById(1L);

        assertThat(postLike).isPresent();
        assertThat(postLike.get().getPost().getPostContent()).isEqualTo("테스트 게시글");
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    void delete_postLike() {
        // 좋아요 삭제
        Optional<PostLike> postLike = postLikeRepository.findById(1L);

        // 값이 존재하는 경우에만 삭제 실행
        postLike.ifPresent(value -> postLikeRepository.deleteByMemberIdAndPostId(value.getMember().getId(), value.getPost().getId()));
        assertThat(postLikeRepository.findById(1L)).isEmpty();

        // 삭제된 엔티티가 존재하지 않음을 확인
        assertThat(postLikeRepository.existsById(1L)).isFalse();
    }

    @Test
    @DisplayName("사용자 좋아요 정보를 무한 스크롤 조회")
    void testUserInfoLikesPaging() {
        Member member = memberRepository.save(Member.builder().loginId("testId").build());
        Post post1 = postRepository.save(Post.builder().postContent("테스트 게시글1").postTitle("타이틀1").postServing("서블").postCookingTime("이름").postCookingLevel("레벨").postLikeCount(0).build());
        Post post2 = postRepository.save(Post.builder().postContent("테스트 게시글2").postTitle("타이틀2").postServing("서블").postCookingTime("이름").postCookingLevel("레벨").postLikeCount(0).build());

        postLikeRepository.save(PostLike.builder().member(member).post(post1).build());
        postLikeRepository.save(PostLike.builder().member(member).post(post2).build());

        int pageSize = 1; // 페이지당 크기
        int pageNumber = 0; // 페이지 번호

        // 사용자의 좋아요 정보를 첫 번째 페이지로 조회
        Slice<UserLikeDto> result = postLikeRepository.userInfoLikes(member.getId(), PageRequest.of(pageNumber, pageSize));
        List<UserLikeDto> content = result.getContent();
        log.info("res={}",result.hasNext());
        log.info("res={}",result.getContent().stream().toList());

        assertThat(content).hasSize(1); // 페이지 크기와 일치하는지 확인
        assertThat(result.hasNext()).isTrue(); // 다음 페이지가 있는지 확인

        // 다음 페이지로 넘어가기
        pageNumber++;
        result = postLikeRepository.userInfoLikes(member.getId(), PageRequest.of(pageNumber, pageSize));
        content = result.getContent();

        assertThat(content).hasSize(1); // 페이지 크기와 일치하는지 확인
        assertThat(result.hasNext()).isFalse(); // 다음 페이지가 없는지 확인
    }
}
