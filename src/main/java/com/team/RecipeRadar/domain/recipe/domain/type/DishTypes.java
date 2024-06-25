package com.team.RecipeRadar.domain.recipe.domain.type;

public enum DishTypes {
    WESTERN("양식"),
    SIDE_DISH("밑반찬"),
    BREAD("빵"),
    SOUP_STEW("국/탕"),
    RICE_PORRIDGE_CAKE("밥/죽/떡"),
    DESSERT("디저트"),
    MAIN_DISH("메인반찬"),
    SNACK("과자"),
    NOODLES_DUMPLINGS("면/만두"),
    JJIGAE("찌개"),
    SEASONING_SAUCE_JAM("양념/소스/잼"),
    TEA_DRINK_ALCOHOL("차/음료/술"),
    SALAD("샐러드"),
    KIMCHI_PICKLES_JANG("김치/젓갈/장류"),
    FUSION("퓨전"),
    SOUP("스프"),
    OTHER("기타");

    private final String korean;

    DishTypes(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }

}
