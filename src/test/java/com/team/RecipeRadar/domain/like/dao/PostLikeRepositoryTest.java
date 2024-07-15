package com.team.RecipeRadar.domain.like.dao;

import com.team.RecipeRadar.domain.like.dao.like.PostLikeRepository;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class PostLikeRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostLikeRepository postLikeRepository;
    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;

    private Member member;
    private List<Post> posts;
    private List<PostLike> postLikes;

    @BeforeEach
    void setUp() {
        member = Member.builder().loginId("testId").build();
        posts = List.of(
                Post.builder().postContent("테스트 게시글").postTitle("타이틀").postServing("서블").postCookingTime("이름").postCookingLevel("레벨").build(),
                Post.builder().postContent("테스트 게시글").postTitle("타이틀").postServing("서블").postCookingTime("이름").postCookingLevel("레벨").build()
        );

        memberRepository.save(member);
        postRepository.saveAll(posts);

        postLikes = List.of(
                PostLike.builder().post(posts.get(0)).member(member).build(),
                PostLike.builder().post(posts.get(1)).member(member).build());
        postLikeRepository.saveAll(postLikes);
    }

    @Test
    @DisplayName("게시글 좋아요 엔티티 저장")
    void save_postLikeEntity() {
        // 좋아요 엔티티 저장 확인
        Optional<PostLike> postLikeOptional = postLikeRepository.findById(postLikes.get(0).getId());

        assertThat(postLikeOptional).isPresent();
        assertThat(postLikeOptional.get().getPost().getPostContent()).isEqualTo("테스트 게시글");
    }

    @Test
    @DisplayName("좋아요 삭제 테스트")
    void delete_postLike() {
        List<PostLike> before = postLikeRepository.findAll();
        postLikeRepository.deleteByPostId(posts.get(0).getId());
        List<PostLike> after = postLikeRepository.findAll();

        assertThat(before).hasSize(2);
        assertThat(after).hasSize(1);
    }

    @Test
    @DisplayName("사용자 좋아요 정보를 무한 스크롤 조회")
    void testUserInfoLikesPaging() {
        Slice<UserLikeDto> result = postLikeRepository.userInfoLikes(member.getId(), null,Pageable.ofSize(1));
        List<UserLikeDto> content = result.getContent();

        assertThat(content).hasSize(1); // 페이지 크기와 일치하는지 확인
        assertThat(result.hasNext()).isTrue(); // 다음 페이지가 있는지 확인

        result = postLikeRepository.userInfoLikes(member.getId(), 1l,Pageable.ofSize(1));
        assertThat(result.hasNext()).isFalse(); // 다음 페이지가 없는지 확인
    }
}
