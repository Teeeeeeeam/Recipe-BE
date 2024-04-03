package com.team.RecipeRadar.domain.like.postLike.dao;

import com.team.RecipeRadar.domain.like.postLike.domain.PostLike;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
}
