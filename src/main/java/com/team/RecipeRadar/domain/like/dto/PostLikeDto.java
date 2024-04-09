package com.team.RecipeRadar.domain.like.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeDto {

    @Schema(hidden = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private Long memberId;
    private Long postId;

}
