package com.team.RecipeRadar.domain.comment.application;

import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    /**
     * 댓글 작성 메서드
     * 댓글 등록시 작성자에게 알림 전송
     */
    public void save(Long postId, String content,Long memberId) {
        Member member = getMember(memberId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_POST));

        Comment savedComment = commentRepository.save(Comment.creatComment(content, member,post));

        notificationService.sendCommentNotification(post,savedComment.getMember().getNickName());       //알림 전송
    }

    @Override
    public Comment findById(long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_COMMENT));
    }

    /**
     * 댓글의 Id와 사용자의 Id를 사용해서 댓글을 삭제한다.
     * 댓글의 작성자가 아닐경우 삭제시에는 ->CommentException 예외를 날린다.
     */
    public void deleteComment(Long commentId, Long memberId) {
        Member member = getMember(memberId);
        Comment comment = getComment(commentId);

        validateCommentOwner(member, comment);
        notificationService.deleteCommentNotification(member.getId(),comment.getPost().getMember().getId(),comment.getId());        //삭제 알림 전송
        commentRepository.deleteMemberId(member.getId(),comment.getId());           //삭제
    }

    /**
     * 댓글을 조회하는 메서드
     * 댓글이 없을시에는 빈 페이지 네이션을 반환
     */
    @Transactional(readOnly = true)
    public Page<CommentDto> commentPage(Long postId,Pageable pageable){
        Page<Comment> comments = commentRepository.findAllByPost_Id(postId, pageable);

        return comments.isEmpty() ?
                new PageImpl<>(Collections.emptyList(), pageable, 0) :
                comments.map(comment -> CommentDto.of(comment));
    }

    /**
     * 댓글 수정 메서드
     * 작성자만이 해당 댓글을 수정 가능 하다.
     */
    @Override
    public void update(Long commentId,String newComment, Long memberId) {
        Member member = getMember(memberId);
        Comment comment = getComment(commentId);

        validateCommentOwner(member,comment);
        comment.update(newComment);
    }
    
    /* 사용자 정보를 조회하는 메서드 */
    private Member getMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
        return member;
    }

    /* 댓글을 조회하는 메서드*/
    private Comment getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_COMMENT));
        return comment;
    }

    /* 댓글을 작성한 작성자인지 검증하는 메서드*/
    private static void validateCommentOwner(Member member, Comment comment) {
        if (!comment.getMember().getId().equals(member.getId()) && !member.getRoles().equals("ROLE_ADMIN"))
            throw new UnauthorizedException("작성자만 삭제할 수 있습니다.");

    }
}
