package com.example.elderease.exercises;

import java.io.Serializable;

public class ExerciseModel implements Serializable {
    private String img;
    private String name;
    private String desc;
    private String benefit;
    private String link;
    private String type;  // Added category field

    public ExerciseModel() {
    }

    public ExerciseModel(String img, String name, String desc, String benefit, String link, String type) {
        this.img = img;
        this.name = name;
        this.desc = desc;
        this.benefit = benefit;
        this.link = link;
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
