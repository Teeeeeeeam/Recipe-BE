package com.team.RecipeRadar.domain.comment.application;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;
    @Mock CommentRepository commentRepository;
    @Mock NotificationService notificationService;

    @InjectMocks CommentServiceImpl commentService;

    private Member member;
    private Member memberFail;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1L).nickName("닉네임").roles("ROLE_USER").build();
        memberFail = Member.builder().id(2L).roles("ROLE_USER").build();
        post = Post.builder().member(member).id(1L).postTitle("제목").build();
        comment = Comment.builder().id(3L).commentContent("댓글").member(member).post(post).build();
    }

    @Test
    @DisplayName("Comment_save 저장테스트")
    void testSave_ValidMemberAndArticle_ReturnsSavedComment() {
        when(memberRepository.findById(eq(member.getId()))).thenReturn(Optional.of(member));
        when(postRepository.findById(eq(post.getId()))).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        commentService.save(post.getId(), "내용!!", member.getId());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("NoDataElementException 오류테스트")
    void testSave_InvalidMemberOrArticle_ThrowsNoSuchElementException() {
        assertThatThrownBy(() -> commentService.save(1L, "변경오류", 1L))
                .isInstanceOf(NoSuchDataException.class);
    }

    @Test
    @DisplayName("댓글 삭제 검증")
    void delete_comment_Test() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        doNothing().when(notificationService).deleteCommentNotification(anyLong(), anyLong(), anyLong());

        commentService.deleteComment(1L, member.getId());

        verify(commentRepository, times(1)).deleteByMemberIdAndCommentId(member.getId(), comment.getId());
    }

    @Test
    @DisplayName("비 작성자가 댓글을 삭제하려고 하는 경우")
    void delete_comment_by_non_author_Test() {
        when(memberRepository.findById(memberFail.getId())).thenReturn(Optional.of(memberFail));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), memberFail.getId()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("댓글 모두조회 페이징 처리 테스트")
    void page_comment_test() {
        List<Comment> comments = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            comments.add(Comment.builder()
                    .id((long) i)
                    .commentContent("테스트 댓글 내용 " + i)
                    .member(member)
                    .post(post)
                    .build());
        }

        PageImpl<Comment> commentPage = new PageImpl<>(comments);
        Pageable pageable = PageRequest.of(0, 5);

        when(commentRepository.findAllByPostId(post.getId(), pageable)).thenReturn(commentPage);

        Page<CommentDto> result = commentService.commentPage(post.getId(), pageable);

        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(1);
        for (int i = 0; i < 10; i++) {
            assertThat(result.getContent().get(i).getCommentContent()).isEqualTo("테스트 댓글 내용 " + (i + 1));
        }
        assertThat(result.getContent().size()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 작성자가 수정 테스트")
    void update_comment() {
        String updateSuccess = "댓글 수정 성공!!";

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        commentService.update(comment.getId(), updateSuccess, member.getId());

        assertThat(comment.getCommentContent()).isEqualTo(updateSuccess);
        assertThat(comment.getCommentContent()).isNotEqualTo("댓글 수정!");
        assertThat(comment.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("댓글 비작성자가 수정 시 오류 발생 테스트")
    void update_comment_throws() {
        when(memberRepository.findById(memberFail.getId())).thenReturn(Optional.of(memberFail));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.update(comment.getId(), "수정!",memberFail.getId()))
                .isInstanceOf(UnauthorizedException.class);
    }
}
