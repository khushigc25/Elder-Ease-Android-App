package com.example.elderease.sos;

public class EmergencyContactModel {
    private String name;
    private String number;

    public EmergencyContactModel() {
        // Default constructor required for Firebase
    }

    public EmergencyContactModel(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
