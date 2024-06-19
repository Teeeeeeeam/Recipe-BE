package com.team.RecipeRadar.global.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Map;

/**
 * NotEmptyMapValue의 유효성 검사를 위한 클래스 해당 클래스는 Map<> 컬렉션의 Value의 대해서 값이 비어있는지 확인한다.
 */
public class mapListValidator implements ConstraintValidator<NotEmptyMapValue, List<Map<String,String>>> {
    @Override
    public boolean isValid(List<Map<String, String>> maps, ConstraintValidatorContext context) {
        if (maps==null || maps.isEmpty()){
            return false;
        }
        for (Map<String, String> map : maps) {
            if (map==null|| map.isEmpty()){
                return false;
            }
            for (String value : map.values()){
                if (value==null || value.trim().isEmpty()){
                    return false;
                }
            }
        }

        return true;
    }
}
