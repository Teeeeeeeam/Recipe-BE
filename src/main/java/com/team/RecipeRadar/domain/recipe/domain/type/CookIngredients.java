package com.team.RecipeRadar.domain.recipe.domain.type;

public enum CookIngredients {

    MEAT("육류"),
    PROCESSED_FOOD("가공식품류"),
    EGGS_DAIRY("달걀/유제품"),
    VEGETABLES("채소류"),
    CHICKEN("닭고기"),
    FLOUR("밀가루"),
    FRUITS("과일류"),
    SEAFOOD("해물류"),
    PORK("돼지고기"),
    BEEF("소고기"),
    RICE("쌀"),
    GRAINS("곡류"),
    DRIED_FISH("건어물류"),
    OTHER("기타"),
    BEANS_NUTS("콩/견과류"),
    MUSHROOMS("버섯류");

    private final String korean;

    CookIngredients(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }

}
