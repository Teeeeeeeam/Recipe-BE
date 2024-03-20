package com.team.RecipeRadar.Service;

import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Comment save(CommentDto commentDto);
    void delete_comment(CommentDto commentDto);

    Page<CommentDto> commentPage(Long id, Pageable pageable);

    void update(Long member_id,Long comment_id,String Content);
}
