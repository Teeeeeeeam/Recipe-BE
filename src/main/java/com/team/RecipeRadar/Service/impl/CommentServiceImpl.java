package com.team.RecipeRadar.Service.impl;

import com.team.RecipeRadar.Entity.Article;
import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.Service.CommentService;
import com.team.RecipeRadar.dto.CommentDto;
import com.team.RecipeRadar.exception.ex.CommentException;
import com.team.RecipeRadar.repository.ArticleRepository;
import com.team.RecipeRadar.repository.CommentRepository;
import com.team.RecipeRadar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;


    /**
     * 댓글 저장하는 기능 -> 게시글과 사용자의 정보를 이요해 Commnet 객체를 생성후 저장
     * @param commentDto
     * @return 저장된 Commnet객체
     */
    public Comment save(CommentDto commentDto) {
        Long member_id = commentDto.getMemberDto().getId();
        Long article_id = commentDto.getArticleDto().getId();

        Optional<Member> member = memberRepository.findById(member_id);
        Optional<Article> article = articleRepository.findById(article_id);

        if (member.isPresent() && article.isPresent()) {        //사용자 정보와 게시글의 정보가 존재할시에만 통과
            Member member1 = member.get();
            Article article1 = article.get();
            LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);        //yyy-dd-mm:hh-MM으로 저장 밀리세컨트는 모두 0초
            Comment build = Comment.builder()                               //댓글 저장
                    .comment_content(commentDto.getComment_content())
                    .member(member1)
                    .article(article1)
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

        Member member = memberRepository.findById(memberDtoId).orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을수 없습니다."));
        Comment comment = commentRepository.findById(commentDtoId).orElseThrow(() -> new NoSuchElementException("해당 댓글 찾을 수없습니다. " + commentDtoId));

        if (comment.getMember().getId().equals(memberDtoId)){           // 댓글을 등록한 사용자 일경우
            commentRepository.deleteMemberId(member.getId(),comment.getId());
        }else
            throw new CommentException("작성자만 삭제할수 있습니다.");      //댓글을 동락한 사용자가 아닐시
    }

}
