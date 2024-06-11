package com.team.RecipeRadar.domain.Image.domain;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private Long id;

    @Column(length = 400)
    private String originFileName;      // 실제 파일명

    @Column(length = 400)
    private String storeFileName;       // DB에 저장될 파일명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public UploadFile(String originFileName, String storeFileName) {
        this.originFileName = originFileName;
        this.storeFileName = storeFileName;
    }

    public void update(String storeFileName, String originFileName) {
        this.storeFileName = storeFileName;
        this.originFileName = originFileName;
    }
}
