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

import javax.persistence.EntityManager;
import java.util.List;
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
    @Autowired
    EntityManager em;

    @BeforeAll
    void init() {
        for (int i = 0; i < 10; i++) {
            Member member = Member.builder().loginId("testId" + i).build();
            Post post = Post.builder().postContent(i + " 번째 게시글").postTitle("타이틀").postServing("서블").postCookingTime("이름").postCookingLevel("레벨").build();

            Member savedMember = memberRepository.save(member);
            Post savedPost = postRepository.save(post);

            PostLike postLike = PostLike.builder().post(savedPost).member(savedMember).build();
            postLikeRepository.save(postLike);
        }
    }

    @Test
    @DisplayName("게시글 좋아요 엔티티 저장")
    void save() {
        List<PostLike> postLikes = postLikeRepository.findAll();

        assertThat(postLikes).hasSize(10);

        for (PostLike postLike : postLikes) {
            assertThat(postLike.getId()).isNotNull();
            assertThat(postLike.getPost()).isNotNull();
            assertThat(postLike.getMember()).isNotNull();
        }

        Optional<PostLike> firstPostLike = postLikeRepository.findById(postLikes.get(0).getId());
        assertThat(firstPostLike).isPresent();
        assertThat(firstPostLike.get().getPost().getPostContent()).isEqualTo("0 번째 게시글");

        Optional<PostLike> lastPostLike = postLikeRepository.findById(postLikes.get(9).getId());
        assertThat(lastPostLike).isPresent();
        assertThat(lastPostLike.get().getPost().getPostContent()).isEqualTo("9 번째 게시글");

        boolean exists = postLikeRepository.existsByMemberIdAndPostId(postLikes.get(0).getMember().getId(), postLikes.get(0).getPost().getId());
        assertThat(exists).isTrue();

        boolean notExists = postLikeRepository.existsByMemberIdAndPostId(postLikes.get(0).getMember().getId(), postLikes.get(1).getPost().getId());
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    void delete_postLikes() {
        // 좋아요 삭제
        Optional<PostLike> postLike = postLikeRepository.findById(1L);

        //람다 사용해서 값이 Optional의 값이 있을때만 삭제 실행
        postLike.ifPresent(value -> postLikeRepository.deleteByMemberIdAndPostId(value.getMember().getId(), value.getPost().getId()));
        assertThat(postLikeRepository.findById(1L)).isEmpty();

        // 일치하지 않는 값 삭제 시도
        Optional<PostLike> like = postLikeRepository.findById(2L);
        assertThat(like).isNotEmpty();
    }
}
