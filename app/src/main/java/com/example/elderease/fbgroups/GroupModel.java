package com.example.elderease.fbgroups;

public class GroupModel {
    private String name;
    private String desc;
    private String url;

    public GroupModel(String name, String desc, String url) {
        this.name = name;
        this.desc = desc;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getUrl() {
        return url;
    }
}
