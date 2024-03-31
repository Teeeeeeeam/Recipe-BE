package com.team.RecipeRadar.domain.recipe.dto;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipePagingResponse {

    private static final long LAST_PAGE= -1L;

    private List<RecipeSearchedDto> contents = new ArrayList<>();
    private Long lastPage;
    private Long nextPage;

    public RecipePagingResponse(List<RecipeSearchedDto> contents, Long lastPage, Long nextPage) {
        this.contents = contents;
        this.lastPage = lastPage;
        this.nextPage = nextPage;
    }

    public static RecipePagingResponse of(Page<Recipe> recipePaging) {
        if (!recipePaging.hasNext()) {
            return RecipePagingResponse.newLastScroll(recipePaging.getContent(), recipePaging.getTotalPages() - 1);
        }
        return RecipePagingResponse.newPagingHasNext(recipePaging.getContent(), recipePaging.getTotalPages() - 1,recipePaging.getPageable().getPageNumber() +1);
    }

    private static RecipePagingResponse newLastScroll(List<Recipe> recipePaging, long lastPage){
        return newPagingHasNext(recipePaging, lastPage, LAST_PAGE);
    }

    private static RecipePagingResponse newPagingHasNext(List<Recipe> recipePaging, long lastPage, long nextPage) {
        return new RecipePagingResponse(getContents(recipePaging), lastPage, nextPage);
    }

    private static List<RecipeSearchedDto> getContents(List<Recipe> ticketPaging) {
        return ticketPaging.stream()
                .map(RecipeSearchedDto::of)
                .collect(Collectors.toList());
    }
}
