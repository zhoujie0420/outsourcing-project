package com.lhqjlb.project.constant;

import lombok.Data;

public enum MedicineCategory {
    COLD(1, "感冒药"),
    STOMACH(2, "胃肠药"),
    CATEGORY(3, "类别药"),
    TRADITIONAL(4, "中药"),
    HEALTH(5, "保健品");

    private String name;
    private int index;
    MedicineCategory(int i, String name) {
        this.name = name;
        this.index = i;
    }

    public String getName() {
        return name;
    }
    public int getIndex() {
        return index;
    }

    public static MedicineCategory getName(int i) {
        for (MedicineCategory value : MedicineCategory.values()) {
            if (value.getIndex() == i) {
                return value;
            }
        }
        return null;
    }
}
