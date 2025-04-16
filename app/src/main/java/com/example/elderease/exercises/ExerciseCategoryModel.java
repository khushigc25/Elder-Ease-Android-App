package com.example.elderease.exercises;

public class ExerciseCategoryModel {
    private String name;
    private String img_url;
    private String type;
    private String benefits;

    public ExerciseCategoryModel() {
    }

    public ExerciseCategoryModel(String name, String img_url, String type, String benefits) {
        this.name = name;
        this.img_url = img_url;
        this.type = type;
        this.benefits = benefits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }
}
