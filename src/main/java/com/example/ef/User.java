package com.example.ef;

public class User {
    String gender, phone, activityLevel, goal, age, height, weight, userName;
    int meals, totalCalories;

    public User() {
    }

    public User(String phone, String gender, String activityLevel, String goal, String age, String height, String weight, int meals, String userName, int totalCalories) {
        this.phone = phone;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.meals = meals;
        this.userName = userName;
        this.totalCalories = totalCalories;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getMeals() {
        return meals;
    }

    public void setMeals(int meals) {
        this.meals = meals;
    }
}
