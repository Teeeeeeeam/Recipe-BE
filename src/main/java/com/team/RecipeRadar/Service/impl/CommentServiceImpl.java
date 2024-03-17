package com.team.RecipeRadar.Service.impl;

import com.team.RecipeRadar.Entity.Article;
import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.Service.CommentService;
import com.team.RecipeRadar.dto.CommentDto;
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

    public Comment save(CommentDto commentDto) {
        Long member_id = commentDto.getMemberDto().getId();
        Long article_id = commentDto.getArticleDto().getId();

        Optional<Member> member = memberRepository.findById(member_id);
        Optional<Article> article = articleRepository.findById(article_id);

        if (member.isPresent() && article.isPresent()) {
            Member member1 = member.get();
            Article article1 = article.get();
            LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);
            Comment build = Comment.builder()
                    .comment_content(commentDto.getComment_content())
                    .member(member1)
                    .article(article1)
                    .created_at(localDateTime)
                    .build();
            return commentRepository.save(build);
        } else {
            throw new NoSuchElementException();
        }
    }

    void delete_comment(Long comment_id,Long member_id){

    }
}
