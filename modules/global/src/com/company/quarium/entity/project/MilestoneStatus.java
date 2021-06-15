package com.company.quarium.entity.project;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum MilestoneStatus implements EnumClass<String> {

    ACTIVE("Active"),
    COMPLETED("Completed");

    private String id;

    MilestoneStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static MilestoneStatus fromId(String id) {
        for (MilestoneStatus at : MilestoneStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}