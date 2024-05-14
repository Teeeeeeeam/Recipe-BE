package com.team.RecipeRadar.domain.recipe.dto.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * List<Map<String,String>>의 대해서 커스텀 @Valid 의 커스텀 Annotation
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = mapListValidator.class)
public @interface NotEmptyMapValue {

    String message() default "List<Map>의 값이 빈 값입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
