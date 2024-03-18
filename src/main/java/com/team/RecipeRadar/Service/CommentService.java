package com.team.RecipeRadar.Service;

import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.dto.CommentDto;

public interface CommentService {
    Comment save(CommentDto commentDto);
    void delete_comment(CommentDto commentDto);
}
