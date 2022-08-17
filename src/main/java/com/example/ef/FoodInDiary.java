package com.example.ef;

public class FoodInDiary {
    String foodName, foodBarcode;
    Integer foodCaloriePerMeal, foodWeightPerMeal, mealNumber;


    public FoodInDiary() {
    }

    public FoodInDiary(String foodName, String foodBarcode, Integer foodCaloriePerMeal, Integer foodWeightPerMeal, Integer mealNumber) {
        this.foodName = foodName;
        this.foodBarcode = foodBarcode;
        this.foodCaloriePerMeal = foodCaloriePerMeal;
        this.foodWeightPerMeal = foodWeightPerMeal;
        this.mealNumber = mealNumber;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodBarcode() {
        return foodBarcode;
    }

    public void setFoodBarcode(String foodBarcode) {
        this.foodBarcode = foodBarcode;
    }

    public Integer getFoodCaloriePerMeal() {
        return foodCaloriePerMeal;
    }

    public void setFoodCaloriePerMeal(Integer foodCaloriePerMeal) {
        this.foodCaloriePerMeal = foodCaloriePerMeal;
    }

    public Integer getFoodWeightPerMeal() {
        return foodWeightPerMeal;
    }

    public void setFoodWeightPerMeal(Integer foodWeightPerMeal) {
        this.foodWeightPerMeal = foodWeightPerMeal;
    }

    public Integer getMealNumber() {
        return mealNumber;
    }

    public void setMealNumber(Integer mealNumber) {
        this.mealNumber = mealNumber;
    }
}
