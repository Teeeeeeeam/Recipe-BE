package com.team.RecipeRadar.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


/*
게시글 댓글기능
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "")
public class Comment {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String comment_content;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime created_at;

    private LocalDateTime updated_at;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @JsonIgnore
    public LocalDateTime getLocDateTime(){
        return this.created_at = LocalDateTime.now().withSecond(0).withNano(0);
    }
    @JsonIgnore
    public LocalDateTime getUpdate_LocDateTime(){
        return this.updated_at = LocalDateTime.now().withSecond(0).withNano(0);
    }

}
