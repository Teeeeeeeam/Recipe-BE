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
public class Post {

    @Id@GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "post_id", updatable = false)
    private Long id;

}
