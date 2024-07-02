package com.team.RecipeRadar.domain.Image.domain;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "idx_post_id", columnList = "post_id"),
        @Index(name = "idx_recipe_id", columnList = "recipe_id"),
        @Index(name = "idx_notice_id", columnList = "notice_id"),
        @Index(name = "idx_question_id", columnList = "question_id"),
        @Index(name = "idx_store_file_name", columnList = "store_file_name"),
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "post")
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private Long id;

    @Column(length = 400)
    private String originFileName;      // 실제 파일명

    @Column(length = 400,name = "store_file_name")
    private String storeFileName;       // DB에 저장될 파일명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id",foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id",foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id",foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Question question;

    public UploadFile(String originFileName, String storeFileName) {
        this.originFileName = originFileName;
        this.storeFileName = storeFileName;
    }

    public void update(String storeFileName, String originFileName) {
        this.storeFileName = storeFileName;
        this.originFileName = originFileName;
    }

    public static<T> UploadFile createUploadFile(List<T> entities, String originFileName, String storeFileName){
        UploadFileBuilder uploadFileBuilder = UploadFile.builder().originFileName(originFileName).storeFileName(storeFileName);

        entities.forEach(entity -> {
            if(entity instanceof Recipe) uploadFileBuilder.recipe((Recipe) entity);
            else if (entity instanceof Post)  uploadFileBuilder.post((Post) entity);
            else if (entity instanceof Notice) uploadFileBuilder.notice((Notice) entity);
            else if (entity instanceof Question) uploadFileBuilder.question((Question) entity);
        });

        return uploadFileBuilder.build();
    }
}
