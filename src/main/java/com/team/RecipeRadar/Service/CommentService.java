package com.team.RecipeRadar.service;

import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.dto.AddCommentRequest;
import com.team.RecipeRadar.dto.UpdateCommentRequest;
import com.team.RecipeRadar.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment save(AddCommentRequest request) {
        return commentRepository.save(request.toEntity());
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Comment findById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(long id) {
        commentRepository.deleteById(id);
    }

    @Transactional
    public Comment update(long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        comment.update(request.getCommentContent());

        return comment;
    }
    public List<Comment> searchComments(String query) {
        return commentRepository.findByCommentContentContainingIgnoreCase(query);
    }
}
