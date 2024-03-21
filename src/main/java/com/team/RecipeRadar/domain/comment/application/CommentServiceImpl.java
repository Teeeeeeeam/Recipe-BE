package com.team.RecipeRadar.domain.comment.application;

import com.team.RecipeRadar.domain.comment.dto.AddCommentRequest;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.dto.UpdateCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

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

    @Transactional
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
}
