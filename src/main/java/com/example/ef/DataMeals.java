package com.example.ef;

import java.util.List;

public class DataMeals {

    private List<FoodInDiary> foodInDiaryList;
    private String foodName;
    private boolean isExpandable;

    public DataMeals(List<FoodInDiary> foodInDiaryList, String foodName) {
        this.foodInDiaryList = foodInDiaryList;
        this.foodName = foodName;
        isExpandable = false;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public List<FoodInDiary> getFoodInDiaryList() {
        return foodInDiaryList;
    }

    public String getFoodName() {
        return foodName;
    }

    public boolean isExpandable() {
        return isExpandable;
    }
}
