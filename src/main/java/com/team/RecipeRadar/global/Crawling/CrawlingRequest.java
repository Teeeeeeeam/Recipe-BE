package com.team.RecipeRadar.global.Crawling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrawlingRequest {
    private String imgUrl;

    private List<String> steps;
}
