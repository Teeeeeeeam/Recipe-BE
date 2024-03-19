package com.team.RecipeRadar.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    @Id@GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

//    @OneToMany(mappedBy = "article")
//    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
