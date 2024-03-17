package com.team.RecipeRadar.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    @Id@GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

}
