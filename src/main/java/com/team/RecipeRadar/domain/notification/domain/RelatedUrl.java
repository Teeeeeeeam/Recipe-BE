package com.team.RecipeRadar.domain.notification.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class RelatedUrl {

    private String url;

    public RelatedUrl(String url){
        this.url = url;
    }
}
