package com.team.RecipeRadar.domain.comment.application;

import com.team.RecipeRadar.domain.comment.dto.AddCommentRequest;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.dto.UpdateCommentRequest;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.team.RecipeRadar.dto.CommentDto;
import com.team.RecipeRadar.exception.ex.CommentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    public Comment save(AddCommentRequest request) {
        return commentRepository.save(request.toEntity());
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment findById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    @Override
    public void delete(long id) {
        commentRepository.deleteById(id);
    }


    @Override
    public Comment update(long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        comment.update(request.getCommentContent());

        return comment;
    }

    @Override
    public List<Comment> searchComments(String query) {
        return commentRepository.findByCommentContentContainingIgnoreCase(query);
    }


    /**
     * 댓글 저장하는 기능 -> 게시글과 사용자의 정보를 이요해 Commnet 객체를 생성후 저장
     * @param commentDto
     * @return 저장된 Commnet객체
     */
    public Comment save(CommentDto commentDto) {
        Long member_id = commentDto.getMemberDto().getId();
        Long article_id = commentDto.getArticleDto().getId();

        Optional<Member> member = memberRepository.findById(member_id);
        Optional<Post> postOptional = postRepository.findById(article_id);

        if (member.isPresent() && postOptional.isPresent()) {        //사용자 정보와 게시글의 정보가 존재할시에만 통과
            Member member1 = member.get();
            Post post = postOptional.get();
            LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);        //yyy-dd-mm:hh-MM으로 저장 밀리세컨트는 모두 0초
            Comment build = Comment.builder()                               //댓글 저장
                    .commentContent(commentDto.getComment_content())
                    .member(member1)
                    .post(post)
                    .created_at(localDateTime)
                    .build();
            return commentRepository.save(build);
        } else {
            throw new NoSuchElementException("회원정보나 게시글을 찾을수 없습니다.");     //사용자 및 게시글이 없을시에는 해당 예외발생
        }
    }

    /**
     * 댓글의 Id와 사용자의 Id를 사용해서 댓글을 삭제한다.
     * 댓글의 작성자가 아닐경우 삭제시에는 ->CommentException 예외를 날린다.
     * @param commentDto
     */
    @Override
    public void delete_comment(CommentDto commentDto) {

        Long memberDtoId = commentDto.getMemberDto().getId();
        Long commentDtoId = commentDto.getId();

        Member member = getMemberThrows(memberDtoId);
        Comment comment = commentRepository.findById(commentDtoId).orElseThrow(() -> new NoSuchElementException("해당 댓글 찾을 수없습니다. " + commentDtoId));

        if (comment.getMember().getId().equals(memberDtoId)){           // 댓글을 등록한 사용자 일경우
            commentRepository.deleteMemberId(member.getId(),comment.getId());
        }else
            throw new CommentException("작성자만 삭제할수 있습니다.");      //댓글을 동락한 사용자가 아닐시
    }

    /**
     * 댓글을 조회하는 메소드
     * @param article_id 게시글 아이디
     * @param pageable
     * @return Dto를 변환한 값을 반환한다.
     */
    @Transactional(readOnly = true)
    public Page<CommentDto> commentPage(Long article_id,Pageable pageable){

        Page<Comment> comments = commentRepository.findAllByPost_Id(article_id, pageable);

        if (!comments.getContent().isEmpty()) {
            return comments.map(comment -> CommentDto.builder().id(comment.getId()).comment_content(comment.getCommentContent()).create_at(comment.getLocDateTime())
                    .memberDto(MemberDto.builder().id(comment.getMember().getId()).nickName(comment.getMember().getNickName()).loginId(comment.getMember().getLoginId()).build())
                    .build());      //스트림 사용
        }else
            throw new CommentException("게시글이 존재하지 않습니다.");
    }

    /**
     * 댓글 수정 기능 -> 작성자만 댓글을 수정가능하다.
     * @param member_id     사용자 id
     * @param comment_id    댓글 id
     * @param comment_content 수정할 댓글 내용
     */
    @Override
    public void update(Long member_id,Long comment_id, String comment_content) {

        Member member = getMemberThrows(member_id);
        Comment comment = commentRepository.findById(comment_id).orElseThrow(() -> new NoSuchElementException("해당 게시물을 찾을수 없습니다."));

        if (comment.getMember().equals(member)){        //Comment 엔티티에 Mmeber가 있는지 없는지 확인
            comment.update(comment_content);
        }else
            throw new CommentException("작성자만 수정 가능합니다.");

    }

    private Member getMemberThrows(Long member_id) {
        return memberRepository.findById(member_id).orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을수 없습니다."));
    }
}
