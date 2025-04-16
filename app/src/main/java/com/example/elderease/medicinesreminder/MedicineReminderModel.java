package com.example.elderease.medicinesreminder;

public class MedicineReminderModel {
    private String id;
    private String medicineName;
    private String medicineType;
    private String dosage;
    private boolean beforeBreakfast;
    private boolean afterBreakfast;
    private boolean beforeLunch;
    private boolean afterLunch;
    private boolean beforeDinner;
    private boolean afterDinner;
    private boolean everyday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;

    public MedicineReminderModel() {
        // Default constructor required for calls to DataSnapshot.getValue(MedicineReminderModel.class)
    }

    // Getters and Setters

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMedicineName() {
        return medicineName;
    }
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    public String getMedicineType() {
        return medicineType;
    }
    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }
    public String getDosage() {
        return dosage;
    }
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    public boolean isBeforeBreakfast() {
        return beforeBreakfast;
    }
    public void setBeforeBreakfast(boolean beforeBreakfast) {
        this.beforeBreakfast = beforeBreakfast;
    }
    public boolean isAfterBreakfast() {
        return afterBreakfast;
    }
    public void setAfterBreakfast(boolean afterBreakfast) {
        this.afterBreakfast = afterBreakfast;
    }
    public boolean isBeforeLunch() {
        return beforeLunch;
    }
    public void setBeforeLunch(boolean beforeLunch) {
        this.beforeLunch = beforeLunch;
    }
    public boolean isAfterLunch() {
        return afterLunch;
    }
    public void setAfterLunch(boolean afterLunch) {
        this.afterLunch = afterLunch;
    }
    public boolean isBeforeDinner() {
        return beforeDinner;
    }
    public void setBeforeDinner(boolean beforeDinner) {
        this.beforeDinner = beforeDinner;
    }
    public boolean isAfterDinner() {
        return afterDinner;
    }
    public void setAfterDinner(boolean afterDinner) {
        this.afterDinner = afterDinner;
    }
    public boolean isEveryday() {
        return everyday;
    }
    public void setEveryday(boolean everyday) {
        this.everyday = everyday;
    }
    public boolean isMonday() {
        return monday;
    }
    public void setMonday(boolean monday) {
        this.monday = monday;
    }
    public boolean isTuesday() {
        return tuesday;
    }
    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }
    public boolean isWednesday() {
        return wednesday;
    }
    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }
    public boolean isThursday() {
        return thursday;
    }
    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }
    public boolean isFriday() {
        return friday;
    }
    public void setFriday(boolean friday) {
        this.friday = friday;
    }
    public boolean isSaturday() {
        return saturday;
    }
    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }
    public boolean isSunday() {
        return sunday;
    }
    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }
}
