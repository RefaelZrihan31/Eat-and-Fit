package com.example.ef;

public class Progress {

    String date, kilogram, imageId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKilogram() {
        return kilogram;
    }

    public void setKilogram(String kilogram) {
        this.kilogram = kilogram;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Progress(String kilogram, String date, String imageId) {
        this.kilogram = kilogram;
        this.date = date;
        this.imageId = imageId;
    }
}
