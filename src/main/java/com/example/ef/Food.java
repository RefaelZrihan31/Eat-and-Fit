package com.example.ef;

public class Food {
    String name, barcode;
    int calories, fats, protein, crabs;

    public Food(String name, String barcode, int calories, int fats, int protein, int crabs) {
        this.name = name;
        this.barcode = barcode;
        this.calories = calories;
        this.fats = fats;
        this.protein = protein;
        this.crabs = crabs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getCrabs() {
        return crabs;
    }

    public void setCrabs(int crabs) {
        this.crabs = crabs;
    }
}
