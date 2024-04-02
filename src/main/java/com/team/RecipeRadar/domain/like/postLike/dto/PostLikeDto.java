package com.team.RecipeRadar.domain.like.postLike.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeDto {

    private Long id;
    private Long memberId;
    private Long postId;


}
