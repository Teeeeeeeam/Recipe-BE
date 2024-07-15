package com.team.RecipeRadar.domain.recipe.domain.type;

// 요리 방법
public enum CookMethods {

    ROASTING("굽기"),
    BOILING("끓이기"),
    STIR_FRYING("볶음"),
    DEEP_FRYING("튀김"),
    OTHER("기타"),
    STEAMING("찜"),
    MIXING("무침"),
    BRAISING("조림"),
    PICKLING("절임"),
    MIXING_BIBIMBAP("비빔"),
    PAN_FRYING("부침"),
    SIMMERING("삶기"),
    SASHIMI("회"),
    BLANCHING("데치기");

    private final String korean;

    CookMethods(String korean) {
        this.korean = korean;
    }

    public String getKorean() {
        return korean;
    }

}
